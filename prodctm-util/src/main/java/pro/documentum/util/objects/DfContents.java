package pro.documentum.util.objects;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.content.IDfContent;
import com.documentum.fc.client.content.impl.ContentStorerExtraData;
import com.documentum.fc.client.content.impl.IContent;
import com.documentum.fc.client.content.impl.IStore;
import com.documentum.fc.client.content.impl.accelerator.FileStoreAccelerator;
import com.documentum.fc.client.content.impl.accelerator.StoredFileInfo;
import com.documentum.fc.client.content.impl.saver.ContentSaverFactory;
import com.documentum.fc.client.content.impl.saver.IContentSaveResult;
import com.documentum.fc.client.content.impl.saver.IContentSaver;
import com.documentum.fc.client.content.internal.IContentDataRelatedIds;
import com.documentum.fc.client.content.internal.IContentStorerExtraData;
import com.documentum.fc.client.content.internal.IStorageApi;
import com.documentum.fc.client.content.internal.StorageApiFactory;
import com.documentum.fc.client.impl.IFormat;
import com.documentum.fc.client.impl.ISysObject;
import com.documentum.fc.client.impl.session.ISession;
import com.documentum.fc.client.impl.typeddata.DynamicallyTypedData;
import com.documentum.fc.client.impl.typeddata.TypedData;
import com.documentum.fc.common.DfException;

import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DfContents {

    private DfContents() {
        super();
    }

    public static IDfContent create(final IDfSession dfSession,
            final File file, final String storageType, final String typeName,
            final String formatName) throws DfException {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            return create(dfSession, is, storageType, typeName, formatName);
        } catch (IOException ex) {
            throw new DfException(ex);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static IDfContent create(final IDfSession dfSession,
            final InputStream stream, final String storageType,
            final String typeName, final String formatName) throws DfException {
        ISession session = (ISession) dfSession;
        IStore store = session.getPredictedStore(storageType, typeName,
                formatName);
        IFormat format = (IFormat) session.getFormat(formatName);
        if (format == null) {
            // I believe this one does always exist
            format = (IFormat) session.getFormat("unknown");
        }
        Objects.requireNonNull(format, "Unknown format " + formatName);
        FileStoreAccelerator accelerator = session.getFileStoreAccelerator();
        IContentSaver contentSaver;
        if (accelerator.isAccelerationPossible(store)) {
            // we are on Content Server and can transfer files directly
            StoredFileInfo storedFileInfo = accelerator.store(store, stream,
                    formatName, null);
            contentSaver = fileSaver(session, storedFileInfo.getPath());
        } else {
            contentSaver = streamSaver(session, stream);
        }
        IContent content = DfObjects.newUnCached(session, "dmr_content");
        content.setStore(store);
        content.setFormat(format);
        content.setFullFormat(format.getName());
        content.setContentSaver(contentSaver);
        return content;
    }

    public static void save(final IDfContent dfContent,
            final IDfSysObject parent) throws DfException {
        IContent content = (IContent) dfContent;
        content.save((ISysObject) parent);
    }

    public static void save(final IDfContent dfContent) throws DfException {
        IContent content = (IContent) dfContent;
        TypedData metaData = new DynamicallyTypedData();
        IContentStorerExtraData extraData = new ContentStorerExtraData(metaData);
        IContentSaver saver = content.getContentSaver();
        IStorageApi storageApi = StorageApiFactory.getInstance();
        IContentDataRelatedIds relatedContentIds = storageApi
                .newContentDataRelatedIds(content.getObjectId(), content
                        .getFormatId(), content.getStore().getObjectId(), -1);
        IContentSaveResult result = saver.save(content.getObjectSession(),
                relatedContentIds, extraData, true);
        content.recordStoreResult(result.getStoreResult(), null);
        content.saveInternal(false, null, null);
    }

    private static IContentSaver fileSaver(final IDfSession dfSession,
            final String path) throws DfException {
        ContentSaverFactory factory = ContentSaverFactory.getInstance();
        return factory.newDirectContentSaver(path, null, Sessions
                .getHostName(dfSession), path);
    }

    private static IContentSaver streamSaver(final IDfSession dfSession,
            final InputStream stream) throws DfException {
        ContentSaverFactory factory = ContentSaverFactory.getInstance();
        return factory.newDirectContentSaver(stream, 0, null, -1, Sessions
                .getHostName(dfSession), null);
    }

}
