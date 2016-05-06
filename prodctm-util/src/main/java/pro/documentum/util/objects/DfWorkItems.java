package pro.documentum.util.objects;

import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DfWorkItems {

    private DfWorkItems() {
        super();
    }

    public static boolean isActive(final IDfWorkitem workitem)
        throws DfException {
        int runtimeState = workitem.getRuntimeState();
        return runtimeState == IDfWorkitem.DF_WI_STATE_DORMANT
                || runtimeState == IDfWorkitem.DF_WI_STATE_ACQUIRED;
    }

    public static boolean isPaused(final IDfWorkitem workitem)
        throws DfException {
        int runtimeState = workitem.getRuntimeState();
        return runtimeState == IDfWorkitem.DF_WI_STATE_PAUSED;
    }

    public static boolean isHalted(final IDfWorkitem workitem)
        throws DfException {
        int runtimeState = workitem.getRuntimeState();
        return runtimeState == IDfWorkitem.DF_WI_STATE_AHALTED
                || runtimeState == IDfWorkitem.DF_WI_STATE_DHALTED
                || runtimeState == IDfWorkitem.DF_WI_STATE_PHALTED;
    }

}
