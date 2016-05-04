package pro.documentum.jdo.util;

import java.util.ArrayList;
import java.util.List;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;

import pro.documentum.jdo.fieldmanager.FetchFieldManager;
import pro.documentum.jdo.fieldmanager.StoreFieldManager;
import pro.documentum.util.objects.changes.ChangesProcessor;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNValues {

    private DNValues() {
        super();
    }

    public static String getObjectId(final IDfTypedObject object) {
        return getString(object, DfDocbaseConstants.R_OBJECT_ID);
    }

    public static String getString(final IDfTypedObject object,
            final String attrName) {
        return getSingleValue(object, attrName, IDfValue.DF_STRING);
    }

    public static <T> T getSingleValue(final IDfTypedObject object,
            final String attrName, final int type) {
        try {
            IDfValue value = object.getValue(attrName);
            return getValue(value, type);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static <T> T getSingleValue(final IDfTypedObject object,
            final String attrName) {
        try {
            IDfValue value = object.getValue(attrName);
            return getValue(value, object.getAttrDataType(attrName));
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static <T> List<T> getRepeatingValue(final IDfTypedObject object,
            final String attrName) {
        try {
            return getRepeatingValue(object, attrName, object
                    .getAttrDataType(attrName));
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getRepeatingValue(final IDfTypedObject object,
            final String attrName, final int type) {
        try {
            List<T> result = new ArrayList<T>();
            for (int i = 0, n = object.getValueCount(attrName); i < n; i++) {
                IDfValue value = object.getRepeatingValue(attrName, i);
                result.add((T) getValue(value, type));
            }
            return result;
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getValue(final IDfValue value, final int type)
        throws DfException {
        switch (type) {
        case IDfValue.DF_BOOLEAN:
            return (T) Boolean.class.cast(value.asBoolean());
        case IDfValue.DF_INTEGER:
            return (T) Integer.class.cast(value.asInteger());
        case IDfValue.DF_DOUBLE:
            return (T) Double.class.cast(value.asDouble());
        case IDfValue.DF_STRING:
            return (T) value.asString();
        case IDfValue.DF_ID:
            return (T) value.asId().getId();
        case IDfValue.DF_TIME:
            IDfTime time = value.asTime();
            if (time == null || time.isNullDate()) {
                return null;
            }
            return (T) time.getDate();
        default:
            throw DfException.newBadDataTypeException(type);
        }
    }

    public static void setNonRelationFields(final IDfPersistentObject object,
            final ObjectProvider op, final Table table, final boolean insert) {
        ExecutionContext ec = op.getExecutionContext();
        AbstractClassMetaData cmd = op.getClassMetaData();
        int[] nonRelPositions = cmd.getNonRelationMemberPositions(ec
                .getClassLoaderResolver(), ec.getMetaDataManager());
        if (!insert) {
            nonRelPositions = DNArrays.getDirtyFields(op, nonRelPositions);
        }
        setFields(object, op, nonRelPositions, table, insert);
    }

    public static void setFields(final IDfPersistentObject object,
            final ObjectProvider op, final int[] fields, final Table table,
            final boolean insert) {
        try {
            StoreFieldManager fm = new StoreFieldManager(op, insert, table);
            op.provideFields(fields, fm);
            ChangesProcessor.process(object, fm.getValues());
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static void loadNonRelationalFields(final ObjectProvider op,
            final IDfPersistentObject object, final int[] fields) {
        AbstractClassMetaData cmd = op.getClassMetaData();
        int[] persistent = DNArrays.getPersistentFields(cmd, fields);
        int[] nonPersistent = DNArrays.getNonPersistentFields(cmd, fields);
        for (int fieldNumber : nonPersistent) {
            op.replaceField(fieldNumber, op.provideField(fieldNumber));
        }
        StoreManager storeMgr = op.getStoreManager();
        Table table = storeMgr.getStoreDataForClass(cmd.getFullClassName())
                .getTable();
        FetchFieldManager fieldManager = new FetchFieldManager(op, object,
                table);
        op.replaceFields(persistent, fieldManager);
        DNVersions.processVersion(op);
    }

    public static void loadNonRelationalFields(final ObjectProvider op,
            final IDfPersistentObject object) {
        AbstractClassMetaData cmd = op.getClassMetaData();
        ExecutionContext ec = op.getExecutionContext();
        int[] nonRelPositions = cmd.getNonRelationMemberPositions(ec
                .getClassLoaderResolver(), ec.getMetaDataManager());
        loadNonRelationalFields(op, object, nonRelPositions);
    }

}
