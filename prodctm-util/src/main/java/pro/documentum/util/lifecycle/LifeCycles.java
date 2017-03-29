package pro.documentum.util.lifecycle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.documentum.fc.client.DfObjectNotFoundException;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfPolicy;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.impl.IPolicy;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfUtil;
import com.documentum.fc.common.DfcMessages;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.impl.MessageHelper;

import pro.documentum.util.ids.DfIdUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class LifeCycles {

    private LifeCycles() {
        super();
    }

    public static IDfPolicy getPolicy(final IDfSysObject document,
            final IDfId policyId, final LifeCycleOp operation)
        throws DfException {
        if (policyId == null) {
            return getPolicy(document, (String) null, operation);
        }
        return getPolicy(document, policyId.getId(), operation);
    }

    public static IDfPolicy getPolicy(final IDfSysObject document,
            final String policyIdOrName, final LifeCycleOp operation)
        throws DfException {
        IDfSession session = document.getSession();
        IDfId policyToFetch = null;
        MessageHelper messageHelper = new MessageHelper(
                "DM_SYSOBJECT_E_NO_POLICY", new Object[] {operation,
                    policyIdOrName, });

        if (DfIdUtil.isPolicyId(policyIdOrName)) {
            policyToFetch = DfId.valueOf(policyIdOrName);
        } else if (DfIdUtil.isNullId(policyIdOrName)) {
            if (operation == LifeCycleOp.DEMOTE
                    || operation == LifeCycleOp.PROMOTE
                    || operation == LifeCycleOp.SUSPEND
                    || operation == LifeCycleOp.RESUME) {
                throw new DfException(messageHelper);
            }
            IDfPersistentObject ddInfo = session
                    .getObjectByQualification("dmi_dd_type_info WHERE type_name='"
                            + document.getTypeName() + "'");
            if (ddInfo != null) {
                policyToFetch = ddInfo.getId("default_policy_id");
            }
        } else {
            policyToFetch = session
                    .getIdByQualification("dm_policy where object_name='"
                            + DfUtil.escapeQuotedString(policyIdOrName) + "'");
        }

        if (!DfIdUtil.isPolicyId(policyToFetch)) {
            throw new DfException(messageHelper);
        }

        try {
            return (IDfPolicy) session.getObject(policyToFetch);
        } catch (DfObjectNotFoundException ex) {
            throw new DfException(messageHelper, ex);
        }
    }

    public static int getStateIndex(final IDfSysObject document,
            final IDfPolicy policy, final String stateNameOrIndex,
            final LifeCycleOp operation) throws DfException {
        String stateName = stateNameOrIndex;
        if (StringUtils.isBlank(stateName)) {
            switch (operation) {
            case PROMOTE:
                stateName = ((IPolicy) policy)
                        .getNextStateNameForStateNo(document.getCurrentState());
                break;
            case DEMOTE:
            case RESUME:
                stateName = ((IPolicy) policy)
                        .getPreviousStateNameForStateNo(document
                                .getCurrentState());
                break;
            case ATTACH:
                return ((IPolicy) policy).getBaseStateIndex();
            case SUSPEND:
                stateName = ((IPolicy) policy)
                        .getExceptionStateNameForStateNo(document
                                .getCurrentState());
                break;
            default:
                throw new DfException("DM_SYSOBJECT_E_BAD_TARGET_STATE",
                        new Object[] {operation.getLowerName(),
                            document.getObjectId(), stateNameOrIndex, });
            }
        }
        if (StringUtils.isBlank(stateName)) {
            throw new DfException("DM_SYSOBJECT_E_BAD_TARGET_STATE",
                    new Object[] {operation.getLowerName(),
                        document.getObjectId(), stateNameOrIndex, });
        }
        if (NumberUtils.isDigits(stateName)) {
            int position = Integer.valueOf(stateName);
            if (position < 0 || position > policy.getStateNoCount()) {
                throw new DfException(
                        DfcMessages.DM_POLICY_E_INVALID_STATE_INDEX,
                        new Object[] {Integer.toString(position),
                            policy.getObjectName(), });
            }
            return position;
        }
        int position = policy.getStateIndex(stateName);
        if (position == -1) {
            throw new DfException(DfcMessages.DM_POLICY_E_INVALID_STATE_NAME,
                    new Object[] {stateName, policy.getObjectName(), });
        }
        return position;
    }

}
