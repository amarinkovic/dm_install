package pro.documentum.jdo.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ArrayMetaData;
import org.datanucleus.metadata.CollectionMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.store.StoreData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNMetaData {

    private DNMetaData() {
        super();
    }

    public static StoreData getStoreData(final ExecutionContext ec,
            final String className) {
        StoreManager storeMgr = ec.getStoreManager();
        StoreData sd = storeMgr.getStoreDataForClass(className);
        if (sd == null) {
            storeMgr.manageClasses(ec.getClassLoaderResolver(), className);
            sd = storeMgr.getStoreDataForClass(className);
        }
        return sd;
    }

    public static AbstractClassMetaData getCollectionElementMetadata(
            final ExecutionContext ec, final AbstractMemberMetaData mmd) {
        ClassLoaderResolver clr = ec.getClassLoaderResolver();
        MetaDataManager mdm = ec.getMetaDataManager();
        if (mmd.hasCollection()) {
            CollectionMetaData collmd = mmd.getCollection();
            if (collmd.elementIsPersistent()) {
                return collmd.getElementClassMetaData(clr, mdm);
            }
        } else if (mmd.hasArray()) {
            ArrayMetaData amd = mmd.getArray();
            if (amd.elementIsPersistent()) {
                return amd.getElementClassMetaData(clr, mdm);
            }
        }
        return null;
    }

    public static Map<String, String> getKnownTables(final ExecutionContext ec) {
        Map<String, String> tables = new HashMap<String, String>();
        for (String className : ec.getMetaDataManager()
                .getClassesWithMetaData()) {
            StoreData storeData = getStoreData(ec, className);
            if (storeData == null) {
                continue;
            }
            Table table = storeData.getTable();
            if (table == null) {
                continue;
            }
            tables.put(table.getName(), className);
        }
        return tables;
    }

    public static String getClassNameForObject(final ExecutionContext ec,
            final IDfPersistentObject object) {
        try {
            return getClassNameForType(ec, object.getType());
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static String getClassNameForType(final ExecutionContext ec,
            final IDfType type) {
        try {
            Map<String, String> tables = getKnownTables(ec);
            IDfType current = type;
            while (current != null) {
                String className = tables.get(current.getName());
                if (StringUtils.isNotBlank(className)) {
                    return className;
                }
                current = current.getSuperType();
            }
            return null;
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static StoreData getStoreData(final ExecutionContext ec,
            final AbstractClassMetaData cmd) {
        return getStoreData(ec, cmd.getFullClassName());
    }

}
