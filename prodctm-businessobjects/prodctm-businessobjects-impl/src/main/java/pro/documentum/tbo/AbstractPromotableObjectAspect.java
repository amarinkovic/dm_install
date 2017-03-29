package pro.documentum.tbo;

import com.documentum.fc.client.DfComputedPolicyStateType;
import com.documentum.fc.client.DfSysObject;
import com.documentum.fc.client.IDfPolicy;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfcMessages;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.impl.MessageHelper;

import pro.documentum.util.IDfSessionInvoker;
import pro.documentum.util.lifecycle.LifeCycleOp;
import pro.documentum.util.lifecycle.LifeCycles;
import pro.documentum.util.logger.Logger;
import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractPromotableObjectAspect extends DfSysObject {

    protected void changeState(final IDfId policyId, final int stateNo,
            final LifeCycleOp operation) throws DfException {
        IDfSessionInvoker<Void> invoker = new IDfSessionInvoker<Void>() {
            @Override
            public Void invoke(final IDfSession session) throws DfException {
                doChangeStateInternal(getPolicyId(), getCurrentState(),
                        policyId, stateNo, operation);
                return null;
            }
        };
        if (changeStateInTransaction()) {
            Sessions.inTransaction(getObjectSession(), invoker);
        } else {
            invoker.invoke(getObjectSession());
        }
    }

    protected boolean changeStateInTransaction() {
        return true;
    }

    protected final void doChangeStateInternal(final IDfId currentPolicyId,
            final int currentState, final IDfId policyId, final int stateNo,
            final LifeCycleOp operation) throws DfException {
        int resumeState = -1;
        if (operation == LifeCycleOp.SUSPEND) {
            resumeState = currentState;
        }
        boolean isAttach = operation == LifeCycleOp.ATTACH;
        setPolicyInfo(policyId, stateNo, resumeState, DfId.DF_NULLID, isAttach);
        doChangeState(currentPolicyId, currentState, policyId, stateNo,
                operation);
    }

    protected abstract void doChangeState(final IDfId currentPolicyId,
            final int currentState, final IDfId policyId, final int stateNo,
            final LifeCycleOp operation) throws DfException;

    @Override
    protected final void doPromote(final String stateNameOrIndex,
            final boolean override, final boolean fTestOnly,
            final Object[] extendedArgs) throws DfException {
        if (fTestOnly) {
            super.doPromote(stateNameOrIndex, override, true, extendedArgs);
            return;
        }
        MessageHelper messageHelper = new MessageHelper(
                "DM_SYSOBJECT_E_BAD_TARGET_STATE", new Object[] {
                    LifeCycleOp.PROMOTE.getLowerName(), getObjectId(),
                    stateNameOrIndex, });
        if (!canPromote()) {
            throw new DfException(messageHelper);
        }
        IDfPolicy policy = LifeCycles.getPolicy(this, getPolicyId(),
                LifeCycleOp.PROMOTE);
        int stateIndex = LifeCycles.getStateIndex(this, policy,
                stateNameOrIndex, LifeCycleOp.PROMOTE);
        int stateNo = policy.getStateNo(stateIndex);
        if (!getNextStateName().equals(policy.getStateName(stateIndex))) {
            throw new DfException(messageHelper);
        }
        changeState(policy.getObjectId(), stateNo, LifeCycleOp.PROMOTE);
    }

    @Override
    protected final void doAttachPolicy(final IDfId policyId,
            final String stateNameOrIndex, final String scope,
            final Object[] extendedArgs) throws DfException {
        verifyPolicyChangeAllowed(LifeCycleOp.ATTACH, true);
        IDfPolicy policy = LifeCycles.getPolicy(this, policyId,
                LifeCycleOp.ATTACH);
        checkApplicable(policy);
        int stateIndex = LifeCycles.getStateIndex(this, policy,
                stateNameOrIndex, LifeCycleOp.ATTACH);
        int stateNo = policy.getStateNo(stateIndex);
        int stateClass = policy.getStateClass(stateIndex);
        if (stateClass != DfComputedPolicyStateType.BASE) {
            if (!policy.getAllowAttach(stateIndex)) {
                throw new DfException("DM_POLICY_E_NON_ATTACH_STATE",
                        new Object[] {stateNameOrIndex, policy.getObjectId(), });
            }
        }
        changeState(policy.getObjectId(), stateNo, LifeCycleOp.ATTACH);
    }

    @Override
    protected final void doDetachPolicy(final Object[] extendedArgs)
        throws DfException {
        verifyPolicyChangeAllowed(LifeCycleOp.DETACH, true);
        changeState(DfId.DF_NULLID, 0, LifeCycleOp.DETACH);
    }

    @Override
    protected final void doDemote(final String stateNameOrIndex,
            final boolean toBase, final Object[] extendedArgs)
        throws DfException {
        verifyPolicyChangeAllowed(LifeCycleOp.DEMOTE, true);
        MessageHelper messageHelper = new MessageHelper(
                "DM_SYSOBJECT_E_BAD_TARGET_STATE", new Object[] {
                    LifeCycleOp.DEMOTE.getLowerName(), getObjectId(),
                    stateNameOrIndex, });
        if (!canDemote()) {
            throw new DfException(messageHelper);
        }
        IDfPolicy policy = LifeCycles.getPolicy(this, getPolicyId(),
                LifeCycleOp.DEMOTE);
        int stateIndex = LifeCycles.getStateIndex(this, policy,
                stateNameOrIndex, LifeCycleOp.DEMOTE);
        int stateNo = policy.getStateNo(stateIndex);
        if (!getPreviousStateName().equals(policy.getStateName(stateIndex))) {
            throw new DfException(messageHelper);
        }
        changeState(policy.getObjectId(), stateNo, LifeCycleOp.DEMOTE);
    }

    @Override
    protected final void doResume(final String stateNameOrIndex,
            final boolean toBase, final boolean override,
            final boolean fTestOnly, final Object[] extendedArgs)
        throws DfException {
        if (fTestOnly) {
            super.doResume(stateNameOrIndex, toBase, override, true,
                    extendedArgs);
            return;
        }
        MessageHelper messageHelper = new MessageHelper(
                "DM_SYSOBJECT_E_BAD_TARGET_STATE", new Object[] {
                    LifeCycleOp.RESUME.getLowerName(), getObjectId(),
                    stateNameOrIndex, });
        verifyPolicyChangeAllowed(LifeCycleOp.RESUME, true);
        if (!canResume()) {
            throw new DfException(messageHelper);
        }
        IDfPolicy policy = LifeCycles.getPolicy(this, getPolicyId(),
                LifeCycleOp.RESUME);
        int stateIndex = LifeCycles.getStateIndex(this, policy,
                stateNameOrIndex, LifeCycleOp.RESUME);
        int stateNo = policy.getStateNo(stateIndex);
        if (!getPreviousStateName().equals(policy.getStateName(stateIndex))) {
            throw new DfException(messageHelper);
        }
        changeState(policy.getObjectId(), stateNo, LifeCycleOp.DEMOTE);
    }

    @Override
    protected final void doSuspend(final String stateNameOrIndex,
            final boolean override, final boolean fTestOnly,
            final Object[] extendedArgs) throws DfException {
        if (fTestOnly) {
            super.doSuspend(stateNameOrIndex, override, true, extendedArgs);
            return;
        }
        verifyPolicyChangeAllowed(LifeCycleOp.SUSPEND, true);
        MessageHelper messageHelper = new MessageHelper(
                "DM_SYSOBJECT_E_BAD_TARGET_STATE", new Object[] {
                    LifeCycleOp.SUSPEND.getLowerName(), getObjectId(),
                    stateNameOrIndex, });
        if (!canSuspend()) {
            throw new DfException(messageHelper);
        }
        IDfPolicy policy = LifeCycles.getPolicy(this, getPolicyId(),
                LifeCycleOp.SUSPEND);
        int stateIndex = LifeCycles.getStateIndex(this, policy,
                stateNameOrIndex, LifeCycleOp.SUSPEND);
        int stateNo = policy.getStateNo(stateIndex);
        if (!getExceptionStateName().equals(policy.getStateName(stateIndex))) {
            throw new DfException(messageHelper);
        }
        changeState(policy.getObjectId(), stateNo, LifeCycleOp.SUSPEND);
    }

    private void verifyPolicyChangeAllowed(final LifeCycleOp operation,
            final boolean isStateTransition) throws DfException {
        if (isNew() || isDirty()) {
            throw new DfException(DfcMessages.DM_POLICY_E_MUST_SAVE,
                    new Object[0]);
        }
    }

    private void checkApplicable(final IDfPolicy policy) throws DfException {
        Logger.debug("Checking whether policy "
                + "{0} is applicable for object {1}", policy.getObjectId(),
                getObjectId());
        int includeSubtypesCount = policy.getIncludeSubtypesCount();
        for (int i = 0, n = policy.getIncludedTypeCount(); i < n; i++) {
            String includeType = policy.getIncludedType(i);
            Logger.debug("Checking type {0}", includeType);
            if (getTypeName().equals(includeType)) {
                Logger.debug("Object {0} has the same type {1}", getObjectId(),
                        includeType);
                return;
            }
            if (i < includeSubtypesCount && policy.getIncludeSubtypes(i)) {
                if (getType().isSubTypeOf(includeType)) {
                    Logger.debug("Object {0} is subtype of {1}", getObjectId(),
                            includeType);
                    return;
                }
            } else {
                Logger.debug("Type index {0} exceeds include_subtype "
                        + "count {1} or include_subtype is false", i,
                        includeSubtypesCount);
            }
        }
        MessageHelper messageHelper = new MessageHelper(
                "DM_POLICY_E_NOT_INCLUDED_TYPE", new Object[] {
                    policy.getObjectId(), getTypeName(), });
        throw new DfException(messageHelper);
    }

}
