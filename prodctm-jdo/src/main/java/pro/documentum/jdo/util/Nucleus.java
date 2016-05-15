package pro.documentum.jdo.util;

import java.lang.reflect.Array;
import java.util.Collection;

import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.DfObjectNotFoundException;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;

import pro.documentum.aspects.DfTransactional;
import pro.documentum.util.ids.DfIdUtil;
import pro.documentum.util.logger.Logger;
import pro.documentum.util.objects.DfObjects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Nucleus {

    private Nucleus() {
        super();
    }

    public static IDfPersistentObject newObject(final IDfSession session,
            final Object id, final AbstractClassMetaData cmd,
            final StoreManager storeMgr) {
        try {
            return doNewObject(session, id, cmd, storeMgr);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    @DfTransactional
    private static IDfPersistentObject doNewObject(final IDfSession session,
            final Object id, final AbstractClassMetaData cmd,
            final StoreManager storeMgr) throws DfException {
        Table table = storeMgr.getStoreDataForClass(cmd.getFullClassName())
                .getTable();
        String objectId = String.valueOf(IdentityUtils
                .getTargetKeyForDatastoreIdentity(id));
        return DfObjects.newObject(session, table.getName(), objectId);
    }

    public static void save(final IDfPersistentObject object) {
        try {
            doSave(object);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    @DfTransactional
    private static void doSave(final IDfPersistentObject object)
        throws DfException {
        Logger.debug("Trying to save object {0}", object.getObjectId());
        if (!object.isDirty()) {
            Logger.debug("Object {0} is not dirty, skipping", object
                    .getObjectId());
            return;
        }
        if (object instanceof IDfSysObject) {
            IDfSysObject sysObject = (IDfSysObject) object;
            Logger.debug("Object {0} is sysobject, saving lock, "
                    + "current lock owner is: {1}", object.getObjectId(),
                    sysObject.getLockOwner());
            sysObject.saveLock();
            return;
        }
        object.save();
    }

    public static String getObjectId(final Object id) {
        String objectId = null;
        if (IdentityUtils.isDatastoreIdentity(id)) {
            objectId = (String) IdentityUtils
                    .getTargetKeyForDatastoreIdentity(id);
        } else if (IdentityUtils.isSingleFieldIdentity(id)) {
            objectId = (String) IdentityUtils
                    .getTargetKeyForSingleFieldIdentity(id);
        }
        if (DfIdUtil.isNotObjectId(objectId)) {
            throw new NucleusObjectNotFoundException("Invalid objectId: "
                    + objectId);
        }
        return objectId;
    }

    public static IDfPersistentObject getObject(final IDfSession session,
            final Object id) {
        return getObject(session, getObjectId(id));
    }

    public static IDfPersistentObject getObject(final IDfSession session,
            final String objectId) {
        try {
            return session.getObject(DfId.valueOf(objectId));
        } catch (DfObjectNotFoundException ex) {
            throw DfExceptions.notFoundException(ex);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static Object newArray(final Class arrayType,
            final Collection<?> values) {
        Class componentClass = arrayType;
        if (componentClass.isArray()) {
            componentClass = arrayType.getComponentType();
        }
        Object array = Array.newInstance(componentClass, values.size());
        int i = 0;
        for (Object value : values) {
            Array.set(array, i, value);
            i++;
        }
        return array;
    }

}
