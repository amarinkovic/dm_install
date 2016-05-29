package pro.documentum.persistence.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ArrayMetaData;
import org.datanucleus.metadata.CollectionMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.store.StoreData;
import org.datanucleus.store.schema.table.Column;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.DocumentumStoreManager;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNMetaData {

    private DNMetaData() {
        super();
    }

    public static StoreData getStoreData(final ExecutionContext ec,
            final String className) {
        DocumentumStoreManager storeMgr = (DocumentumStoreManager) ec
                .getStoreManager();
        StoreData sd = storeMgr.getStoreDataForClass(className);
        if (sd == null) {
            storeMgr.manageClasses(ec, className);
            sd = storeMgr.getStoreDataForClass(className);
        }
        return sd;
    }

    public static AbstractClassMetaData getElementMetadata(
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

    public static String getElementClassName(final ExecutionContext ec,
            final AbstractMemberMetaData mmd) {
        if (mmd.hasCollection()) {
            CollectionMetaData collmd = mmd.getCollection();
            return collmd.getElementType();
        } else if (mmd.hasArray()) {
            ArrayMetaData amd = mmd.getArray();
            return amd.getElementType();
        }
        return null;
    }

    public static Map<String, String> getKnownTables(final ExecutionContext ec) {
        Map<String, String> tables = new HashMap<>();
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

    public static String getFirstColumn(final Table table,
            final AbstractMemberMetaData mmd) {
        Column column = table.getMemberColumnMappingForMember(mmd).getColumn(0);
        return getSelectColumns(column, false).get(0);
    }

    public static String getFirstEmbeddedColumn(final Table table,
            final List<AbstractMemberMetaData> mmd) {
        Column column = table.getMemberColumnMappingForEmbeddedMember(mmd)
                .getColumn(0);
        return getSelectColumns(column, false).get(0);
    }

    public static List<String> getSelectColumns(final Column column) {
        return getSelectColumns(column, true);
    }

    public static List<String> getSelectColumns(final Column column,
            final boolean defaultFetchGroup) {
        List<String> result = new ArrayList<>();
        MemberColumnMapping mcm = column.getMemberColumnMapping();
        if (mcm == null) {
            result.add(column.getName());
            return result;
        }
        AbstractMemberMetaData mmd = mcm.getMemberMetaData();
        if (defaultFetchGroup) {
            if (!mmd.isDefaultFetchGroup() && !mmd.isSerialized()) {
                return result;
            }
        }
        ColumnMetaData[] columnMetaDatum = null;
        if (mmd.hasContainer()) {
            ElementMetaData emd = mmd.getElementMetaData();
            if (emd != null) {
                ColumnMetaData[] data = emd.getColumnMetaData();
                if (data != null && data.length > 0) {
                    columnMetaDatum = data;
                }
            }
        }
        if (columnMetaDatum == null) {
            columnMetaDatum = mmd.getColumnMetaData();
        }
        for (ColumnMetaData c : columnMetaDatum) {
            result.add(c.getName());
        }
        return result;
    }

}
