package pro.documentum.persistence.common.query.result.persistent;

import org.datanucleus.FetchPlan;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.FieldValues;

import pro.documentum.persistence.common.fieldmanager.FetchFieldManager;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class HollowFieldValues implements FieldValues {

    private final FetchFieldManager _fm;
    private final int[] _members;

    HollowFieldValues(final FetchFieldManager fm, final int[] members) {
        _fm = fm;
        _members = members;
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
