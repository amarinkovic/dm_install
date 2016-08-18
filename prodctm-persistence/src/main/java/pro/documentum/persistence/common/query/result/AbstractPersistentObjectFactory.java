package pro.documentum.persistence.common.query.result;

import org.datanucleus.ExecutionContext;
import org.datanucleus.FetchPlan;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.identity.SCOID;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.FieldValues;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfTypedObject;

import pro.documentum.persistence.common.fieldmanager.FetchFieldManager;
import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNVersions;
import pro.documentum.persistence.common.util.Nucleus;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractPersistentObjectFactory<E> implements
        IResultObjectFactory<E> {

    private final AbstractClassMetaData _metaData;

    private final int[] _members;

    private final boolean _ignoreCache;

    protected AbstractPersistentObjectFactory(
            final AbstractClassMetaData metaData, final int[] members,
            final boolean ignoreCache) {
        _metaData = metaData;
        _members = members;
        _ignoreCache = ignoreCache;
    }

    @SuppressWarnings("unchecked")
    private E findObject(final ExecutionContext ec, final FetchFieldManager fm,
            final Object id, final Class<E> type) {
        FieldValues fv = new HollowFieldValues(fm);
        E pc = (E) ec.findObject(id, fv, type, _ignoreCache, false);
        return DNVersions.processVersion(ec, pc);
    }

    protected E getPojoForDBObjectForCandidate(final IDfTypedObject dbObject,
            final ExecutionContext ec) {
        AbstractClassMetaData cmd = getMetaData(ec, dbObject);
        FetchFieldManager fieldManager = getFetchFieldManager(dbObject, cmd, ec);
        Class<E> type = getClass(cmd, ec);
        Object objectId = getObjectId(dbObject, ec, cmd, fieldManager);
        return findObject(ec, fieldManager, objectId, type);
    }

    private Object getObjectId(final IDfTypedObject dbObject,
            final ExecutionContext ec, final AbstractClassMetaData cmd,
            final FetchFieldManager fm) {
        switch (cmd.getIdentityType()) {
        case APPLICATION:
            return IdentityUtils.getApplicationIdentityForResultSetRow(ec, cmd,
                    null, false, fm);
        case DATASTORE:
            return Nucleus.getIdentity(ec, cmd, dbObject);
        default:
            return new SCOID(cmd.getFullClassName());
        }

    }

    private FetchFieldManager getFetchFieldManager(
            final IDfTypedObject dbObject, final AbstractClassMetaData cmd,
            final ExecutionContext ec) {
        Table table = DNMetaData.getStoreData(ec, cmd).getTable();
        return new FetchFieldManager(ec, dbObject, cmd, table);
    }

    @SuppressWarnings("unchecked")
    private Class<E> getClass(final AbstractClassMetaData cmd,
            final ExecutionContext ec) {
        return (Class<E>) ec.getClassLoaderResolver().classForName(
                cmd.getFullClassName());
    }

    protected AbstractClassMetaData getMetaData(final ExecutionContext ec,
            final IDfTypedObject dbObject) {
        return DNMetaData.getActual(dbObject, ec, _metaData);
    }

    private class HollowFieldValues implements FieldValues {

        private final FetchFieldManager _fm;

        HollowFieldValues(final FetchFieldManager fm) {
            _fm = fm;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public void fetchFields(final ObjectProvider op) {
            op.replaceFields(_members, _fm);
        }

        @Override
        @SuppressWarnings("rawtypes")
        public void fetchNonLoadedFields(final ObjectProvider op) {
            op.replaceNonLoadedFields(_members, _fm);
        }

        public FetchPlan getFetchPlanForLoading() {
            return null;
        }

    }

}
