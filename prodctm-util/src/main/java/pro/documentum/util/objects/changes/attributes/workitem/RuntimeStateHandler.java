package pro.documentum.util.objects.changes.attributes.workitem;

import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfWorkflow;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfException;

import pro.documentum.util.objects.DfWorkItems;
import pro.documentum.util.objects.changes.attributes.Depends;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Depends(on = {PerformerHandler.class })
public class RuntimeStateHandler extends AbstractWorkItemAttributeHandler {

    public RuntimeStateHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return attrNames.contains("r_runtime_state");
    }

    @Override
    public boolean doApply(final IDfWorkitem object, final Map<String, ?> values)
        throws DfException {
        Integer newState = (Integer) values.remove("r_runtime_state");
        if (DfWorkItems.isActive(object)) {
            changeActiveState(object, newState);
            return false;
        }

        if (DfWorkItems.isPaused(object)) {
            changePausedState(object, newState);
            return false;
        }

        if (DfWorkItems.isHalted(object)) {
            changeHaltedState(object, newState);
        }
        return false;
    }

    private void changeActiveState(final IDfWorkitem workitem,
            final int newState) throws DfException {
        int currentState = workitem.getRuntimeState();
        switch (newState) {
        case IDfWorkitem.DF_WI_STATE_ACQUIRED:
            workitem.acquire();
            break;
        case IDfWorkitem.DF_WI_STATE_FINISHED:
            if (currentState == IDfWorkitem.DF_WI_STATE_DORMANT) {
                changeActiveState(workitem, IDfWorkitem.DF_WI_STATE_ACQUIRED);
            }
            workitem.complete();
            break;
        case IDfWorkitem.DF_WI_STATE_PAUSED:
            workitem.pause();
            break;
        case IDfWorkitem.DF_WI_STATE_DHALTED:
            IDfWorkflow workflow = getWorkflow(workitem);
            workflow.halt(workitem.getActSeqno());
            break;
        default:
            break;
        }
    }

    private void changePausedState(final IDfWorkitem workitem,
            final int newState) throws DfException {
        switch (newState) {
        case IDfWorkitem.DF_WI_STATE_DORMANT:
        case IDfWorkitem.DF_WI_STATE_ACQUIRED:
            workitem.resume();
            break;
        default:
            break;
        }
    }

    private void changeHaltedState(final IDfWorkitem workitem,
            final int newState) throws DfException {
        IDfWorkflow workflow = getWorkflow(workitem);
        workflow.restart(workitem.getActSeqno());
    }

    private IDfWorkflow getWorkflow(final IDfWorkitem workitem)
        throws DfException {
        IDfSession session = workitem.getSession();
        return (IDfWorkflow) session.getObject(workitem.getWorkflowId());
    }

}
