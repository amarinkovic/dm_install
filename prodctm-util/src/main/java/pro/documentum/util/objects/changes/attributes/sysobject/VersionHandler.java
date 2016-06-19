package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.operations.IDfCheckinOperation;

import pro.documentum.util.logger.Logger;
import pro.documentum.util.versions.Versions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class VersionHandler extends AbstractSysObjectAttributeHandler {

    public static final Set<String> VERSION_ATTRIBUTES;

    static {
        VERSION_ATTRIBUTES = new HashSet<>();
        VERSION_ATTRIBUTES.add("r_lock_owner");
        VERSION_ATTRIBUTES.add("r_version_label");
    }

    public VersionHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, VERSION_ATTRIBUTES);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean doApply(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        boolean isLast = isLastOp(object, values);
        boolean isEmptyLockOwner = isEmptyLockOwner(object, values);
        boolean isCheckIn = isCheckIn(object, values);

        if (isCheckIn) {
            if (!isLast) {
                Logger.debug("Postponing check in operation for object {0}",
                        object.getObjectId());
                return true;
            }
            Logger.debug("Performing check in operation for object {0}",
                    object.getObjectId());
            object.checkin(!isEmptyLockOwner, getVersionLabels(object, values));
            removeKey(values, VERSION_ATTRIBUTES);
            return false;
        }

        if (object.isCheckedOut() && isEmptyLockOwner) {
            if (isLast) {
                if (object.isDirty()) {
                    Logger.debug("Performing save "
                            + "operation for object {0}", object.getObjectId());
                    object.save();
                } else {
                    Logger.debug("Performing cancel check out "
                            + "operation for object {0}", object.getObjectId());
                    object.cancelCheckout();
                }
                removeKey(values, VERSION_ATTRIBUTES);
            } else {
                Logger.debug("Postponing save/unlock operation for object {0}",
                        object.getObjectId());
            }
            return !isLast;
        }

        if (!object.isCheckedOut() && hasLockOwner(object, values)) {
            Logger.debug("Performing check out operation for object {0}",
                    object.getObjectId());
            object.checkout();
            removeKey(values, VERSION_ATTRIBUTES);
            return false;
        }

        setVersionLabel(object, values);

        removeKey(values, VERSION_ATTRIBUTES);

        return false;
    }

    protected void setVersionLabel(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        // todo
    }

    private boolean isLastOp(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        int required = 0;
        for (String attr : VERSION_ATTRIBUTES) {
            if (values.containsKey(attr)) {
                required++;
            }
        }
        return values.keySet().size() == required;
    }

    @SuppressWarnings("unchecked")
    private boolean isCheckIn(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        if (!hasVersion(object, values)) {
            return false;
        }
        if (!object.isCheckedOut()) {
            return false;
        }
        List<String> versionLabels = (List<String>) values
                .get("r_version_label");
        if (versionLabels == null || versionLabels.isEmpty()) {
            return false;
        }
        String labels = getVersionLabels(object, values);
        if (StringUtils.isBlank(labels)) {
            return false;
        }
        return Versions.getNextVersion(object, labels) != IDfCheckinOperation.SAME_VERSION;
    }

    @SuppressWarnings("unchecked")
    private String getVersionLabels(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        List<String> versionLabels = (List<String>) values
                .get("r_version_label");
        if (versionLabels == null || versionLabels.isEmpty()) {
            return null;
        }
        return StringUtils.join(versionLabels, ",");
    }

    private boolean isEmptyLockOwner(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        if (!hasLockOwner(object, values)) {
            return false;
        }
        String lockOwner = (String) values.get("r_lock_owner");
        return StringUtils.isBlank(lockOwner);
    }

    private boolean hasVersion(final IDfSysObject object,
            final Map<String, ?> values) {
        return values.containsKey("r_version_label");
    }

    private boolean hasLockOwner(final IDfSysObject object,
            final Map<String, ?> values) {
        return values.containsKey("r_lock_owner");
    }

}
