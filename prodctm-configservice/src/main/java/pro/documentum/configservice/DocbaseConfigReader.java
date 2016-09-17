package pro.documentum.configservice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

import pro.documentum.util.IDfSessionInvoker;
import pro.documentum.util.ids.DfIdUtil;
import pro.documentum.util.logger.Logger;
import pro.documentum.util.queries.DfIterator;
import pro.documentum.util.queries.Queries;
import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
final class DocbaseConfigReader extends AbstractConfigReader {

    public static final String APPLICATION_CONFIG_FILE = "app.xml";

    private DocbaseConfigReader() {
        super();
    }

    public static DocbaseConfigReader getInstance(
            final IConfigProvider configProvider) {
        DocbaseConfigReader reader = new DocbaseConfigReader();
        reader.initialize(configProvider.getRootFolderPath(),
                configProvider.getAppName());
        return reader;
    }

    public long getSysObjectsChecksum(final IDfSession session,
            final String query) throws DfException {
        final Checksum checksum = new CRC32();
        try (DfIterator iterator = Queries.execute(session, query)) {
            while (iterator.hasNext()) {
                doHandleConfigCheckSum(iterator.next(), checksum);
            }
        }
        return checksum.getValue();
    }

    public Long getCurrentChecksum() throws DfException {
        return Sessions.withGlobalRegistry(new ConfigChecksum());
    }

    @Override
    protected String getAppConfig() {
        List<String> configs = queryConfigs(getApplicationConfigQuery(getRootFolderPath()));
        if (configs == null || configs.isEmpty()) {
            Logger.error("Unable to locate application "
                    + "config app.xml in {0} folder", getRootFolderPath());
            return null;
        }
        return configs.get(0);
    }

    @Override
    protected List<String> getConfigs() {
        return queryConfigs(getConfigsQuery(getRootFolderPath()));
    }

    private List<String> queryConfigs(final String query) {
        try {
            return Sessions.withGlobalRegistry(new ConfigList(query));
        } catch (Exception ex) {
            Logger.error(ex);
        }
        return Collections.emptyList();
    }

    private Long doGetCurrentChecksum(final IDfSession session)
        throws DfException {
        return getSysObjectsChecksum(session,
                getCurrentChecksumQuery(getRootFolderPath()));
    }

    private List<String> doQueryConfigs(final IDfSession session,
            final String query) throws DfException {
        final List<String> result = new ArrayList<>();
        try (DfIterator iterator = Queries.execute(session, query)) {
            while (iterator.hasNext()) {
                doHandleNewConfigPath(iterator.next(), result);
            }
        }
        return result;
    }

    private String getConfigsQuery(final String folderPath) {
        return "SELECT FOR READ r_object_id FROM dm_sysobject WHERE folder('"
                + folderPath
                + "', DESCEND) AND a_content_type='xml' AND object_name<>'"
                + APPLICATION_CONFIG_FILE + "'";
    }

    private String getApplicationConfigQuery(final String folderPath) {
        return "SELECT FOR READ r_object_id FROM dm_sysobject WHERE folder('"
                + folderPath + "') AND a_content_type='xml' AND object_name='"
                + APPLICATION_CONFIG_FILE + "'";
    }

    private String getCurrentChecksumQuery(final String folderPath) {
        return "SELECT FOR READ r_object_id, i_vstamp FROM dm_sysobject WHERE folder('"
                + folderPath
                + "', DESCEND) AND a_content_type='xml' ORDER BY r_object_id";
    }

    protected void doHandleConfigCheckSum(final IDfTypedObject row,
            final Checksum checksum) throws DfException {
        if (!row.hasAttr(DfDocbaseConstants.R_OBJECT_ID)) {
            return;
        }
        if (!row.hasAttr(DfDocbaseConstants.I_VSTAMP)) {
            return;
        }
        IDfId objectId = row.getId(DfDocbaseConstants.R_OBJECT_ID);
        if (!DfIdUtil.isObjectId(objectId)) {
            return;
        }
        String vstamp = String.valueOf(row.getInt(DfDocbaseConstants.I_VSTAMP));
        checksum.update(objectId.getId().getBytes(), 0, 16);
        checksum.update(vstamp.getBytes(), 0, vstamp.length());
    }

    protected void doHandleNewConfigPath(final IDfTypedObject row,
            final List<String> result) throws DfException {
        try {
            IDfSession session = row.getSession();
            IDfSysObject object = (IDfSysObject) session.getObject(row
                    .getId(DfDocbaseConstants.R_OBJECT_ID));
            if (object.getContentSize() <= 0) {
                return;
            }
            File tempFile = File.createTempFile(
                    String.valueOf(System.currentTimeMillis()), ".xml",
                    getTempDirectory());
            tempFile.deleteOnExit();
            result.add(object.getFile(tempFile.getAbsolutePath()));
        } catch (IOException ex) {
            throw new DfException(ex);
        }
    }

    private File getTempDirectory() throws IOException {
        String temp = System.getProperty("java.io.tmpdir");
        temp += File.separator + getAppName();
        File tempDir = new File(temp);
        if (!tempDir.exists()) {
            boolean result = tempDir.mkdir();
            if (!result) {
                throw new IOException("Unable to create temporary directory: "
                        + temp);
            }
        }
        return tempDir;
    }

    private class ConfigChecksum implements IDfSessionInvoker<Long> {

        ConfigChecksum() {
            super();
        }

        @Override
        public Long invoke(final IDfSession session) throws DfException {
            return doGetCurrentChecksum(session);
        }

    }

    private class ConfigList implements IDfSessionInvoker<List<String>> {

        private final String _query;

        ConfigList(final String query) {
            _query = query;
        }

        @Override
        public List<String> invoke(final IDfSession session) throws DfException {
            return doQueryConfigs(session, _query);
        }

    }

}
