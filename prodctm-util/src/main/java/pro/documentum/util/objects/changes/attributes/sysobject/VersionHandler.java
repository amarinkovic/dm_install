package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.Collections;
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
        Set<String> versionAttributes = new HashSet<>();
        versionAttributes.add("r_lock_owner");
        versionAttributes.add("r_version_label");
        VERSION_ATTRIBUTES = Collections.unmodifiableSet(versionAttributes);
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
    public boolean doApply(final IDfSysObject sysObject,
            final Map<String, ?> values) throws DfException {
        boolean isLast = isLastOp(values);
        boolean isEmptyLockOwner = isEmptyLockOwner(sysObject, values);
        boolean isCheckIn = isCheckIn(sysObject, values);

        if (isCheckIn) {
            if (!isLast) {
                Logger.debug("Postponing check in operation for object {0}",
                        sysObject.getObjectId());
                return true;
            }
            Logger.debug("Performing check in operation for object {0}",
                    sysObject.getObjectId());
            sysObject.checkin(!isEmptyLockOwner, getVersionLabels(values));
            removeKey(values, VERSION_ATTRIBUTES);
            return false;
        }

        if (sysObject.isCheckedOut() && isEmptyLockOwner) {
            if (!isLast) {
                Logger.debug("Postponing save/unlock operation for object {0}",
                        sysObject.getObjectId());
                return true;
            }
            if (sysObject.isDirty()) {
                Logger.debug("Performing save operation for object {0}",
                        sysObject.getObjectId());
                sysObject.save();
            } else {
                Logger.debug("Performing cancel check out "
                        + "operation for object {0}", sysObject.getObjectId());
                sysObject.cancelCheckout();
            }
            removeKey(values, VERSION_ATTRIBUTES);
            return false;
        }

        if (!sysObject.isCheckedOut() && hasLockOwner(values)) {
            Logger.debug("Performing check out operation for object {0}",
                    sysObject.getObjectId());
            sysObject.checkout();
            removeKey(values, VERSION_ATTRIBUTES);
            return false;
        }

        setVersionLabel(sysObject, values);

        removeKey(values, VERSION_ATTRIBUTES);

        return false;
    }

    protected void setVersionLabel(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        // todo
    }

    private boolean isLastOp(final Map<String, ?> values) throws DfException {
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
        if (!hasVersion(values)) {
            return false;
        }
        if (!object.isCheckedOut()) {
            return false;
        }
        String labels = getVersionLabels(values);
        if (StringUtils.isBlank(labels)) {
            return false;
        }
        return Versions.getNextVersion(object, labels) != IDfCheckinOperation.SAME_VERSION;
    }

    @SuppressWarnings("unchecked")
    private String getVersionLabels(final Map<String, ?> values)
        throws DfException {
        List<String> versionLabels = (List<String>) values
                .get("r_version_label");
        if (versionLabels.isEmpty()) {
            return null;
        }
        return StringUtils.join(versionLabels, ",");
    }

    private boolean isEmptyLockOwner(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        if (!hasLockOwner(values)) {
            return false;
        }
        String lockOwner = (String) values.get("r_lock_owner");
        return StringUtils.isBlank(lockOwner);
    }

    private boolean hasVersion(final Map<String, ?> values) {
        return values.containsKey("r_version_label");
    }

    private boolean hasLockOwner(final Map<String, ?> values) {
        return values.containsKey("r_lock_owner");
    }

}
