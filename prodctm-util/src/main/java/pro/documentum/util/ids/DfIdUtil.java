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
        if (StringUtils.isBlank(objectId)) {
            return true;
        }
        return DfId.DF_NULLID_STR.equals(objectId);
    }

    public static boolean isObjectId(final IDfId id) {
        if (id == null) {
            return false;
        }
        return isObjectIdInternal(id);
    }

    private static boolean isObjectIdInternal(final IDfId objectId) {
        if (!objectId.isObjectId()) {
            return false;
        }
        if (objectId.getTypePart() > 0x70) {
            return false;
        }
        return objectId.getNumericDocbaseId() <= 0xFFFFFF;
    }

    public static boolean isObjectId(final String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        return isObjectIdInternal(getId(id));
    }

    public static boolean isNotObjectId(final String id) {
        return !isObjectId(id);
    }

    public static boolean isNotObjectId(final IDfId id) {
        return !isObjectId(id);
    }

    public static boolean isPackageId(final IDfId packageId) {
        if (!isObjectId(packageId)) {
            return false;
        }
        return packageId.getTypePart() == IDfId.DM_PACKAGE;
    }

    public static boolean isPackageId(final String packageId) {
        return isPackageId(getId(packageId));
    }

    public static boolean isWorkitemId(final IDfId workitemId) {
        if (!isObjectId(workitemId)) {
            return false;
        }
        return workitemId.getTypePart() == IDfId.DM_WORKITEM;
    }

    public static boolean isWorkitemId(final String workitemId) {
        return isWorkitemId(getId(workitemId));
    }

    public static boolean isWorkflowId(final IDfId workflowId) {
        if (!isObjectId(workflowId)) {
            return false;
        }
        return workflowId.getTypePart() == IDfId.DM_WORKFLOW;
    }

    public static boolean isWorkflowId(final String workflowId) {
        return isWorkflowId(getId(workflowId));
    }

    public static boolean isQueueItemId(final IDfId queueItemId) {
        if (!isObjectId(queueItemId)) {
            return false;
        }
        return queueItemId.getTypePart() == IDfId.DM_QUEUE_ITEM;
    }

    public static boolean isQueueItemId(final String queueItemId) {
        return isQueueItemId(getId(queueItemId));
    }

    public static boolean isProcessId(final IDfId processId) {
        if (!isObjectId(processId)) {
            return false;
        }
        return processId.getTypePart() == IDfId.DM_PROCESS;
    }

    public static boolean isProcessId(final String processId) {
        return isProcessId(getId(processId));
    }

    public static boolean isActivityId(final IDfId activityId) {
        if (!isObjectId(activityId)) {
            return false;
        }
        return activityId.getTypePart() == IDfId.DM_ACTIVITY;
    }

    public static boolean isActivityId(final String activityId) {
        return isActivityId(getId(activityId));
    }

    public static boolean isFolderId(final IDfId folderId) {
        if (!isObjectId(folderId)) {
            return false;
        }
        return folderId.getTypePart() == IDfId.DM_FOLDER;
    }

    public static boolean isFolderId(final String folderId) {
        return isFolderId(getId(folderId));
    }

    public static boolean isAliasSetId(final IDfId aliasSetId) {
        if (!isObjectId(aliasSetId)) {
            return false;
        }
        return aliasSetId.getTypePart() == IDfId.DM_ALIAS_SET;
    }

    public static boolean isAliasSetId(final String aliasSetId) {
        return isAliasSetId(getId(aliasSetId));
    }

    public static boolean isDmDocumentId(final IDfId documentId) {
        if (!isObjectId(documentId)) {
            return false;
        }
        return documentId.getTypePart() == IDfId.DM_DOCUMENT;
    }

    public static boolean isDmDocumentId(final String documentId) {
        return isDmDocumentId(getId(documentId));
    }

    public static boolean isDmSysobjectId(final IDfId documentId) {
        if (!isObjectId(documentId)) {
            return false;
        }
        return documentId.getTypePart() == IDfId.DM_SYSOBJECT;
    }

    public static boolean isDmSysobjectId(final String documentId) {
        return isDmSysobjectId(getId(documentId));
    }

    public static boolean isDmRelationId(final IDfId relationId) {
        if (!isObjectId(relationId)) {
            return false;
        }
        return relationId.getTypePart() == IDfId.DM_RELATION;
    }

    public static boolean isDmRelationId(final String relationId) {
        return isDmRelationId(getId(relationId));
    }

    public static boolean isCabinetId(final IDfId cabinetId) {
        if (!isObjectId(cabinetId)) {
            return false;
        }
        return cabinetId.getTypePart() == IDfId.DM_CABINET;
    }

    public static boolean isCabinetId(final String cabinetId) {
        return isCabinetId(getId(cabinetId));
    }

    public static boolean isCabinetOrFolderId(final IDfId objectId) {
        return isFolderId(objectId) || isCabinetId(objectId);
    }

    public static boolean isCabinetOrFolderId(final String objectId) {
        return isFolderId(objectId) || isCabinetId(objectId);
    }

    public static boolean isPolicyId(final IDfId policyId) {
        if (!isObjectId(policyId)) {
            return false;
        }
        return policyId.getTypePart() == IDfId.DM_POLICY;
    }

    public static boolean isPolicyId(final String objectId) {
        return isPolicyId(getId(objectId));
    }

}
