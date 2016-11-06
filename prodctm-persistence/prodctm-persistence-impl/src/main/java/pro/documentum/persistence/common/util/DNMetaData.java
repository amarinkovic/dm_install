package pro.documentum.persistence.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ArrayMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.CollectionMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.EmbeddedMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.metadata.InterfaceMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.StoreData;
import org.datanucleus.store.schema.table.Column;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfType;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.StoreManagerImpl;
import pro.documentum.util.java.Classes;
import pro.documentum.util.logger.Logger;
import pro.documentum.util.types.DfTypes;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNMetaData {

    private DNMetaData() {
        super();
    }

    public static StoreData getStoreData(final ExecutionContext ec,
            final String className) {
        StoreManagerImpl storeMgr = (StoreManagerImpl) ec.getStoreManager();
        StoreData sd = storeMgr.getStoreDataForClass(className);
        if (sd == null) {
            storeMgr.manageClasses(ec, className);
            sd = storeMgr.getStoreDataForClass(className);
        }
        if (sd == null) {
            Logger.error("NO TABLE: " + className);
        }
        return sd;
    }

    public static Table getTable(final StoreManagerImpl storeManager,
            final AbstractClassMetaData cmd) {
        return storeManager.getStoreDataForClass(cmd.getFullClassName())
                .getTable();
    }

    public static Table getTable(final StoreManagerImpl storeManager,
            final String className) {
        return storeManager.getStoreDataForClass(className).getTable();
    }

    public static Table getTable(final ExecutionContext ec,
            final String className) {
        return getStoreData(ec, className).getTable();
    }

    public static Table getTable(final ExecutionContext ec,
            final AbstractClassMetaData cmd) {
        return getStoreData(ec, cmd).getTable();
    }

    public static Class<?> getElementClass(final AbstractMemberMetaData mmd) {
        return Classes.getClass(getElementClassName(mmd));
    }

    public static String getElementClassName(final AbstractMemberMetaData mmd) {
        if (mmd.hasContainer()) {
            if (mmd.hasCollection()) {
                CollectionMetaData collmd = mmd.getCollection();
                return collmd.getElementType();
            } else if (mmd.hasArray()) {
                ArrayMetaData amd = mmd.getArray();
                return amd.getElementType();
            }
        } else {
            return mmd.getTypeName();
        }
        return null;
    }

    public static Map<String, String> getKnownTables(final ExecutionContext ec) {
        Map<String, String> tables = new HashMap<>();
        MetaDataManager mdm = ec.getMetaDataManager();
        Collection<String> names = mdm.getClassesWithMetaData();
        for (String className : names) {
            AbstractClassMetaData cmd = mdm.getMetaDataForClass(className,
                    ec.getClassLoaderResolver());
            if (StringUtils.isBlank(cmd.getTable())) {
                continue;
            }
            if (cmd instanceof InterfaceMetaData) {
                continue;
            }
            if (cmd instanceof ClassMetaData) {
                if (((ClassMetaData) cmd).isAbstract()) {
                    continue;
                }
            }
            StoreData storeData = getStoreData(ec, cmd);
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
            final IDfTypedObject object) {
        try {
            IDfType type = DfTypes.getType(object);
            if (type != null) {
                return getClassNameForType(ec, type);
            }
            return null;
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
        ColumnMetaData[] columnMetaDatum = Objects
                .requireNonNull(getColumnMetaData(mmd));
        for (ColumnMetaData c : columnMetaDatum) {
            result.add(c.getName());
        }
        return result;
    }

    public static AbstractClassMetaData getActual(
            final IDfTypedObject dbObject, final ExecutionContext ec,
            final AbstractClassMetaData classMetaData) {
        AbstractClassMetaData cmd = classMetaData;
        String className;
        if (cmd == null || !cmd.hasDiscriminatorStrategy()) {
            className = getClassNameForObject(ec, dbObject);
        } else {
            Table table = getStoreData(ec, classMetaData).getTable();
            String propName = table.getDiscriminatorColumn().getName();
            String value = DNValues.getString(dbObject, propName);
            className = getClassNameFromDiscriminator(ec, cmd, value);
        }
        if (className == null) {
            return cmd;
        }
        if (cmd == null || !className.equals(cmd.getFullClassName())) {
            cmd = getMetaDataForClass(ec, className);
        }
        return cmd;
    }

    public static String getClassNameFromDiscriminator(
            final ExecutionContext ec, final AbstractClassMetaData cmd,
            final String discriminatorValue) {
        return ec.getMetaDataManager().getClassNameFromDiscriminatorValue(
                discriminatorValue, cmd.getDiscriminatorMetaData());
    }

    public static AbstractClassMetaData getMetaDataForClass(
            final ExecutionContext ec, final String className) {
        return ec.getMetaDataManager().getMetaDataForClass(className,
                ec.getClassLoaderResolver());
    }

    public static boolean isPersistent(final AbstractMemberMetaData mmd) {
        return mmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT;
    }

    public static String getColumnName(final AbstractMemberMetaData embMmd) {
        List<String> names = getColumnNames(embMmd);
        if (names == null || names.isEmpty()) {
            throw new IllegalStateException("No columnMapping for "
                    + embMmd.getName());
        }
        return names.get(0);
    }

    public static ColumnMetaData[] getColumnMetaData(
            final AbstractMemberMetaData mmd) {
        ColumnMetaData[] cmd = null;
        ElementMetaData elmd = mmd.getElementMetaData();
        if (elmd != null) {
            cmd = elmd.getColumnMetaData();
        }
        if (cmd == null || cmd.length == 0) {
            cmd = mmd.getColumnMetaData();
        }
        return cmd;
    }

    public static AbstractMemberMetaData[] getEmbeddedMemberMetaData(
            final AbstractMemberMetaData mmd) {
        ElementMetaData elmd = mmd.getElementMetaData();
        AbstractMemberMetaData[] ammd = null;
        if (elmd != null) {
            EmbeddedMetaData emmd = elmd.getEmbeddedMetaData();
            if (emmd != null) {
                ammd = emmd.getMemberMetaData();
            }
        }
        if (ammd == null || ammd.length == 0) {
            EmbeddedMetaData emmd = mmd.getEmbeddedMetaData();
            if (emmd != null) {
                ammd = emmd.getMemberMetaData();
            }
        }
        return ammd;
    }

    public static List<String> getColumnNames(final AbstractMemberMetaData mmd) {
        List<String> result = new ArrayList<>();
        ColumnMetaData[] columnMetaData = Objects
                .requireNonNull(getColumnMetaData(mmd));
        for (ColumnMetaData cmd : columnMetaData) {
            result.add(cmd.getName());
        }
        return result;
    }

    public static boolean isEmbedded(final ExecutionContext context,
            final AbstractMemberMetaData mmd) {
        ClassLoaderResolver clr = context.getClassLoaderResolver();
        RelationType relationType = mmd.getRelationType(clr);
        return MetaDataUtils.getInstance().isMemberEmbedded(
                context.getMetaDataManager(), clr, mmd, relationType, null);
    }

    public static List<String> getSelectColumns(final ExecutionContext context,
            final Column column, final boolean defaultFetchGroup) {
        List<String> result = new ArrayList<>();
        MemberColumnMapping mcm = column.getMemberColumnMapping();
        if (mcm == null) {
            result.add(column.getName());
            return result;
        }
        AbstractMemberMetaData mmd = mcm.getMemberMetaData();
        if (defaultFetchGroup) {
            if (!mmd.isEmbedded() && !mmd.isSerialized()) {
                return result;
            }
        }
        // todo
        if (result.addAll(getSelectColumns(context, mmd))) {
            return result;
        }
        for (Column c : mcm.getColumns()) {
            result.add(c.getName());
        }
        return result;
    }

    public static List<String> getSelectColumns(final ExecutionContext context,
            final AbstractMemberMetaData mmd) {
        AbstractMemberMetaData[] mmds;
        if (isEmbedded(context, mmd)) {
            mmds = getEmbeddedMemberMetaData(mmd);
        } else {
            mmds = new AbstractMemberMetaData[] {mmd, };
        }
        List<String> result = new ArrayList<>();
        for (AbstractMemberMetaData ammd : mmds) {
            ColumnMetaData[] cmds = getColumnMetaData(ammd);
            for (ColumnMetaData cmd : cmds) {
                result.add(cmd.getName());
            }
        }
        return result;
    }

}
