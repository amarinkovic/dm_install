package pro.documentum.util.ids;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DfIdUtil {

    private DfIdUtil() {
        super();
    }

    public static IDfId getId(final String id) {
        return DfId.valueOf(id);
    }

    public static boolean isNullId(final String objectId) {
        return StringUtils.isBlank(objectId)
                || DfId.DF_NULLID_STR.equals(objectId);
    }

    public static boolean isObjectId(final IDfId id) {
        return id != null && id.isObjectId();
    }

    public static boolean isObjectId(final String id) {
        return !StringUtils.isBlank(id) && DfId.isObjectId(id);
    }

    public static boolean isNotObjectId(final String id) {
        return !isObjectId(id);
    }

    public static boolean isNotObjectId(final IDfId id) {
        return !DfId.isObjectId(id.getId());
    }

    public static boolean isPackageId(final IDfId packageId) {
        return isObjectId(packageId)
                && packageId.getTypePart() == IDfId.DM_PACKAGE;
    }

    public static boolean isWorkitemId(final IDfId workitemId) {
        return isObjectId(workitemId)
                && workitemId.getTypePart() == IDfId.DM_WORKITEM;
    }

    public static boolean isWorkitemId(final String workitemId) {
        return isWorkitemId(getId(workitemId));
    }

    public static boolean isWorkflowId(final String workflowId) {
        return isWorkflowId(getId(workflowId));
    }

    public static boolean isWorkflowId(final IDfId workflowId) {
        return isObjectId(workflowId)
                && workflowId.getTypePart() == IDfId.DM_WORKFLOW;
    }

    public static boolean isQueueItemId(final String queueItemId) {
        return isQueueItemId(getId(queueItemId));
    }

    public static boolean isQueueItemId(final IDfId queueItemId) {
        return isObjectId(queueItemId)
                && queueItemId.getTypePart() == IDfId.DM_QUEUE_ITEM;
    }

    public static boolean isProcessId(final IDfId processId) {
        return isObjectId(processId)
                && processId.getTypePart() == IDfId.DM_PROCESS;
    }

    public static boolean isActivityId(final IDfId activityId) {
        return isObjectId(activityId)
                && activityId.getTypePart() == IDfId.DM_ACTIVITY;
    }

    public static boolean isFolderId(final IDfId folderId) {
        return isObjectId(folderId)
                && folderId.getTypePart() == IDfId.DM_FOLDER;
    }

    public static boolean isFolderId(final String folderId) {
        return isFolderId(getId(folderId));
    }

    public static boolean isAliasSetId(final IDfId aliasSetId) {
        return isObjectId(aliasSetId)
                && aliasSetId.getTypePart() == IDfId.DM_ALIAS_SET;
    }

    public static boolean isDmDocumentId(final IDfId documentId) {
        return isObjectId(documentId)
                && documentId.getTypePart() == IDfId.DM_DOCUMENT;
    }

    public static boolean isDmDocumentId(final String documentId) {
        return isObjectId(documentId)
                && getId(documentId).getTypePart() == IDfId.DM_DOCUMENT;
    }

    public static boolean isDmSysobjectId(final IDfId documentId) {
        return isObjectId(documentId)
                && documentId.getTypePart() == IDfId.DM_SYSOBJECT;
    }

    public static boolean isDmSysobjectId(final String documentId) {
        return isObjectId(documentId)
                && getId(documentId).getTypePart() == IDfId.DM_SYSOBJECT;
    }

    public static boolean isDmRelationtId(final String documentId) {
        return isObjectId(documentId)
                && getId(documentId).getTypePart() == IDfId.DM_RELATION;
    }

    public static boolean isCabinetId(final IDfId cabinetId) {
        return isObjectId(cabinetId)
                && cabinetId.getTypePart() == IDfId.DM_CABINET;
    }

    public static boolean isCabinetId(final String cabinetId) {
        return isFolderId(getId(cabinetId));
    }

    public static boolean isCabinetOrFolderId(final IDfId objectId) {
        return isFolderId(objectId) || isCabinetId(objectId);
    }

    public static boolean isCabinetOrFolderId(final String objectId) {
        IDfId folderId = getId(objectId);
        return isFolderId(folderId) || isCabinetId(folderId);
    }

    public static boolean isPolicyId(final IDfId policyId) {
        return isObjectId(policyId)
                && policyId.getTypePart() == IDfId.DM_POLICY;
    }

    public static boolean isPolicyId(final String objectId) {
        IDfId policyId = getId(objectId);
        return isObjectId(objectId)
                && policyId.getTypePart() == IDfId.DM_POLICY;
    }

}
