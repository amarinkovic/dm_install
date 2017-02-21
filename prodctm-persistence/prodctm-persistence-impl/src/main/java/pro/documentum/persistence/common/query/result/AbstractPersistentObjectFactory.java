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

    private final ExecutionContext _ec;

    private final AbstractClassMetaData _cmd;

    private final int[] _members;

    private final boolean _ignoreCache;

    protected AbstractPersistentObjectFactory(final ExecutionContext ec,
            final AbstractClassMetaData cmd, final int[] members,
            final boolean ignoreCache) {
        _ec = ec;
        _cmd = cmd;
        _members = members;
        _ignoreCache = ignoreCache;
    }

    protected boolean isIgnoreCache() {
        return _ignoreCache;
    }

    @SuppressWarnings("unchecked")
    private E findObject(final FetchFieldManager fm, final Object id,
            final Class<E> type) {
        FieldValues fv = new HollowFieldValues(fm);
        E pc = (E) _ec.findObject(id, fv, type, isIgnoreCache(), false);
        return DNVersions.processVersion(_ec, pc);
    }

    protected E getPojoForDBObjectForCandidate(final IDfTypedObject dbObject) {
        return getPojoForDBObjectForCandidate(dbObject, getMetaData(dbObject));
    }

    protected E getPojoForDBObjectForCandidate(final IDfTypedObject dbObject,
            final AbstractClassMetaData cmd) {
        FetchFieldManager fieldManager = getFetchFieldManager(dbObject, cmd);
        Class<E> type = getClass(cmd);
        Object objectId = getObjectId(dbObject, cmd, fieldManager);
        return findObject(fieldManager, objectId, type);
    }

    private Object getObjectId(final IDfTypedObject dbObject,
            final AbstractClassMetaData cmd, final FetchFieldManager fm) {
        switch (cmd.getIdentityType()) {
        case APPLICATION:
            return getApplicationIdentity(cmd, fm);
        case DATASTORE:
            return Nucleus.getIdentity(_ec, cmd, dbObject);
        default:
            return new SCOID(cmd.getFullClassName());
        }
    }

    private Object getApplicationIdentity(final AbstractClassMetaData cmd,
            final FetchFieldManager fm) {
        return IdentityUtils.getApplicationIdentityForResultSetRow(_ec, cmd,
                null, false, fm);
    }

    private FetchFieldManager getFetchFieldManager(
            final IDfTypedObject dbObject, final AbstractClassMetaData cmd) {
        Table table = DNMetaData.getTable(_ec, cmd);
        return new FetchFieldManager(_ec, dbObject, cmd, table);
    }

    @SuppressWarnings("unchecked")
    private Class<E> getClass(final AbstractClassMetaData cmd) {
        return (Class<E>) _ec.getClassLoaderResolver().classForName(
                cmd.getFullClassName());
    }

    protected AbstractClassMetaData getMetaData(final IDfTypedObject dbObject) {
        return DNMetaData.getActual(dbObject, _ec, _cmd);
    }

    protected Table getTable(final AbstractClassMetaData cmd) {
        return DNMetaData.getTable(_ec, cmd);
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
