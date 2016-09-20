package pro.documentum.configservice;

import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.impl.ISysObject;
import com.documentum.fc.common.DfException;
import com.documentum.operations.IDfDeleteOperation;

import pro.documentum.junit.DfcTestSupport;
import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractConfigServiceTest extends DfcTestSupport {

    public static final String FOLDER = "/System/Config/prodctm-test";

    public static final String APP = "prodctm-test";

    @Override
    protected void doPostSetup() throws Exception {
        IDfSession txSession = getSession();
        IDfSession session = Sessions.brandNew(txSession.getSessionManager(),
                txSession.getDocbaseName());
        cleanup(session);
        createFolders(session);
        loadResource(session, "/app.xml", "app.xml");
        loadResource(session, "/docbaseconfig.xml", "docbaseconfig.xml");
    }

    @Override
    protected void doPreTearDown() throws Exception {
        IDfSession txSession = getSession();
        IDfSession session = Sessions.brandNew(txSession.getSessionManager(),
                txSession.getDocbaseName());
        cleanup(session);
    }

    protected void cleanup(IDfSession session) throws DfException {
        IDfFolder folder = session.getFolderByPath(FOLDER);
        if (folder != null) {
            IDfDeleteOperation operation = CLIENT_X.getDeleteOperation();
            operation.setDeepFolders(true);
            operation.enableDeepDeleteFolderChildren(true);
            operation.setSession(session);
            operation.setVersionDeletionPolicy(IDfDeleteOperation.ALL_VERSIONS);
            operation.add(folder);
            operation.execute();
        }
    }

    protected void createFolders(IDfSession session) throws DfException {
        IDfFolder folder = session.getFolderByPath("/System/Config");
        if (folder == null) {
            folder = (IDfFolder) session.newObject("dm_folder");
            folder.setObjectName("Config");
            folder.link("/System");
            folder.save();
        }
        folder = session.getFolderByPath(FOLDER);
        if (folder == null) {
            folder = (IDfFolder) session.newObject("dm_folder");
            folder.setObjectName("prodctm-test");
            folder.link("/System/Config");
            folder.save();
        }
    }

    protected void loadResource(IDfSession session, String resourceName,
            String objectName) throws DfException {
        IDfSysObject object = (IDfSysObject) session.newObject("dm_sysobject");
        object.link(FOLDER);
        object.setObjectName(objectName);
        ((ISysObject) object).setStream(
                getClass().getResourceAsStream(resourceName), 0, "xml");
        object.save();
    }

}
