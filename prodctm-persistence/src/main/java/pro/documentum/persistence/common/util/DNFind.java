package pro.documentum.persistence.common.util;

import org.datanucleus.ExecutionContext;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.identity.SCOID;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.FieldValues;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfTypedObject;

import pro.documentum.persistence.common.fieldmanager.FetchFieldManager;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNFind {

    private DNFind() {
        super();
    }

    @SuppressWarnings("unchecked")
    public static <T> T findObject(final ExecutionContext ec,
            final boolean ignoreCache, final int[] fpMembers,
            final FetchFieldManager fm, final Object id, final Class<T> type) {
        FieldValues fv = new HollowFieldValues(fpMembers, fm);
        return (T) ec.findObject(id, fv, type, ignoreCache, false);
    }

    public static <T> T getPojoForDBObjectForCandidate(
            final IDfTypedObject dbObject, final ExecutionContext ec,
            final AbstractClassMetaData classMetaData, final int[] fpMembers,
            final boolean ignoreCache) {
        AbstractClassMetaData cmd = DNMetaData.getActual(dbObject, ec,
                classMetaData);
        T pojo = null;
        if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            pojo = getObjectUsingApplicationIdForDBObject(dbObject, cmd, ec,
                    ignoreCache, fpMembers);
        } else if (cmd.getIdentityType() == IdentityType.DATASTORE) {
            pojo = getObjectUsingDatastoreIdForDBObject(dbObject, cmd, ec,
                    ignoreCache, fpMembers);
        } else {
            pojo = getObjectUsingNondurableIdForDBObject(dbObject, cmd, ec,
                    ignoreCache, fpMembers);
        }
        return pojo;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObjectUsingApplicationIdForDBObject(
            final IDfTypedObject dbObject, final AbstractClassMetaData cmd,
            final ExecutionContext ec, final boolean ignoreCache,
            final int[] fpMembers) {
        FetchFieldManager fm = getFetchFieldManager(dbObject, cmd, ec);
        Object id = IdentityUtils.getApplicationIdentityForResultSetRow(ec,
                cmd, null, false, fm);
        Class<T> type = getClass(cmd, ec);
        T pc = findObject(ec, ignoreCache, fpMembers, fm, id, type);
        DNVersions.processVersion(ec, pc);
        return pc;
    }

    private static FetchFieldManager getFetchFieldManager(
            final IDfTypedObject dbObject, final AbstractClassMetaData cmd,
            final ExecutionContext ec) {
        Table table = DNMetaData.getStoreData(ec, cmd).getTable();
        return new FetchFieldManager(ec, dbObject, cmd, table);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObjectUsingDatastoreIdForDBObject(
            final IDfTypedObject dbObject, final AbstractClassMetaData cmd,
            final ExecutionContext ec, final boolean ignoreCache,
            final int[] fpMembers) {
        Object idKey = DNValues.getObjectId(dbObject);
        FetchFieldManager fm = getFetchFieldManager(dbObject, cmd, ec);
        Object id = ec.getNucleusContext().getIdentityManager()
                .getDatastoreId(cmd.getFullClassName(), idKey);
        Class<T> type = getClass(cmd, ec);
        T pc = findObject(ec, ignoreCache, fpMembers, fm, id, type);
        DNVersions.processVersion(ec, pc);
        return pc;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClass(final AbstractClassMetaData cmd,
            final ExecutionContext ec) {
        return (Class<T>) ec.getClassLoaderResolver().classForName(
                cmd.getFullClassName());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObjectUsingNondurableIdForDBObject(
            final IDfTypedObject dbObject, final AbstractClassMetaData cmd,
            final ExecutionContext ec, final boolean ignoreCache,
            final int[] fpMembers) {
        Table table = DNMetaData.getStoreData(ec, cmd).getTable();
        Object id = new SCOID(cmd.getFullClassName());
        FetchFieldManager fm = new FetchFieldManager(ec, dbObject, cmd, table);
        Class<T> type = getClass(cmd, ec);
        T pc = findObject(ec, ignoreCache, fpMembers, fm, id, type);
        DNVersions.processVersion(ec, pc);
        return pc;
    }

}
