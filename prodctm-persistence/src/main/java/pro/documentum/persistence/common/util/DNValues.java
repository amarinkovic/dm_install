package pro.documentum.persistence.common.util;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.fieldmanager.FetchFieldManager;
import pro.documentum.persistence.common.fieldmanager.StoreFieldManager;
import pro.documentum.util.IDfSessionInvoker;
import pro.documentum.util.convert.Converter;
import pro.documentum.util.objects.changes.ChangesProcessor;
import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNValues {

    private static final Converter CONVERTER = Converter.getInstance();

    private DNValues() {
        super();
    }

    public static int getValueCount(final IDfTypedObject object,
            final String attrName) {
        try {
            return object.getValueCount(attrName);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static boolean hasAttr(final IDfTypedObject object,
            final String attrName) {
        try {
            return object.hasAttr(attrName);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static String getObjectId(final IDfTypedObject object) {
        return getString(object, DfDocbaseConstants.R_OBJECT_ID);
    }

    public static String getString(final IDfTypedObject object,
            final String attrName) {
        return getSingleValue(object, attrName, String.class);
    }

    public static <T> T getSingleValue(final IDfTypedObject object,
            final String attrName, final Class<?> type) {
        return getSingleValue(object, attrName, 0, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSingleValue(final IDfTypedObject object,
            final String attrName, final int index, final Class<?> type) {
        try {
            return (T) CONVERTER.fromDataStore(object, attrName, type, index);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static void setNonRelationFields(final IDfPersistentObject object,
            final ObjectProvider<?> op, final Table table, final boolean insert) {
        ExecutionContext ec = op.getExecutionContext();
        AbstractClassMetaData cmd = op.getClassMetaData();
        int[] nonRelPositions = cmd.getNonRelationMemberPositions(
                ec.getClassLoaderResolver(), ec.getMetaDataManager());
        if (!insert) {
            nonRelPositions = DNArrays.getDirtyFields(op, nonRelPositions);
        }
        setFields(object, op, nonRelPositions, table, insert);
    }

    public static void setFields(final IDfPersistentObject object,
            final ObjectProvider<?> op, final int[] fields, final Table table,
            final boolean insert) {
        try {
            Sessions.inTransaction(object.getSession(),
                    new IDfSessionInvoker<Void>() {
                        @Override
                        public Void invoke(final IDfSession session)
                            throws DfException {
                            doSetFields(object, op, fields, table, insert);
                            return null;
                        }
                    });
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    private static void doSetFields(final IDfPersistentObject object,
            final ObjectProvider<?> op, final int[] fields, final Table table,
            final boolean insert) throws DfException {
        StoreFieldManager fm = new StoreFieldManager(op, object, insert, table);
        op.provideFields(fields, fm);
        ChangesProcessor.process(object, fm.getValues());
    }

    public static void loadNonRelationalFields(final ObjectProvider<?> op,
            final IDfPersistentObject object, final int[] fields) {
        AbstractClassMetaData cmd = op.getClassMetaData();
        int[] persistent = DNArrays.getPersistentFields(cmd, fields);
        int[] nonPersistent = DNArrays.getNonPersistentFields(cmd, fields);
        for (int fieldNumber : nonPersistent) {
            op.replaceField(fieldNumber, op.provideField(fieldNumber));
        }
        StoreManager storeMgr = op.getStoreManager();
        Table table = DNMetaData.getTable(storeMgr, cmd.getFullClassName());
        FetchFieldManager fieldManager = new FetchFieldManager(op, object,
                table);
        op.replaceFields(persistent, fieldManager);
        DNVersions.processVersion(op);
    }

    public static void loadNonRelationalFields(final ObjectProvider<?> op,
            final IDfPersistentObject object) {
        AbstractClassMetaData cmd = op.getClassMetaData();
        ExecutionContext ec = op.getExecutionContext();
        int[] nonRelPositions = cmd.getNonRelationMemberPositions(
                ec.getClassLoaderResolver(), ec.getMetaDataManager());
        loadNonRelationalFields(op, object, nonRelPositions);
    }

}
