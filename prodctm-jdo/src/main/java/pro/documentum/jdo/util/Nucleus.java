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

import pro.documentum.util.ids.DfIdUtil;
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
            Table table = storeMgr.getStoreDataForClass(cmd.getFullClassName())
                    .getTable();
            String objectId = String.valueOf(IdentityUtils
                    .getTargetKeyForDatastoreIdentity(id));
            return DfObjects.newObject(session, table.getName(), objectId);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static void save(final IDfPersistentObject object) {
        try {
            if (!object.isDirty()) {
                return;
            }
            if (object instanceof IDfSysObject) {
                ((IDfSysObject) object).saveLock();
            }
            object.save();
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
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
