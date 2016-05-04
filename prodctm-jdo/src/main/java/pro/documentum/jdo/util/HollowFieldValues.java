package pro.documentum.jdo.util;

import org.datanucleus.FetchPlan;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.FieldValues;

import pro.documentum.jdo.fieldmanager.FetchFieldManager;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class HollowFieldValues implements FieldValues {

    private final int[] _fpMembers;

    private final FetchFieldManager _fm;

    public HollowFieldValues(final int[] fpMembers, final FetchFieldManager fm) {
        _fpMembers = fpMembers;
        _fm = fm;
    }

    public void fetchFields(final ObjectProvider op) {
        op.replaceFields(_fpMembers, _fm);
    }

    public void fetchNonLoadedFields(final ObjectProvider op) {
        op.replaceNonLoadedFields(_fpMembers, _fm);
    }

    public FetchPlan getFetchPlanForLoading() {
        return null;
    }

}
