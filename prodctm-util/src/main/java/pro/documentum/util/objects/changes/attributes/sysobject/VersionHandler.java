package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.operations.IDfCheckinOperation;

import pro.documentum.util.versions.Versions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class VersionHandler extends AbstractSysObjectAttributeHandler {

    public static final Set<String> LOCK_ATTRIBUTES;

    static {
        LOCK_ATTRIBUTES = new HashSet<String>();
        LOCK_ATTRIBUTES.add("r_lock_owner");
        LOCK_ATTRIBUTES.add("r_version_label");
    }

    public VersionHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        for (String attrName : LOCK_ATTRIBUTES) {
            if (attrNames.contains(attrName)) {
                return true;
            }
        }
        return false;
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
                return true;
            }
            object.checkin(!isEmptyLockOwner, getVersionLabels(object, values));
            cleanAttrs(values);
            return false;
        }

        if (object.isCheckedOut() && isEmptyLockOwner) {
            if (isLast) {
                if (object.isDirty()) {
                    object.save();
                } else {
                    object.cancelCheckout();
                }
                cleanAttrs(values);
            }
            return !isLast;
        }

        if (!object.isCheckedOut() && hasLockOwner(object, values)) {
            object.checkout();
            cleanAttrs(values);
            return false;
        }

        return false;
    }

    private void cleanAttrs(final Map<String, ?> values) {
        for (String attrName : LOCK_ATTRIBUTES) {
            values.remove(attrName);
        }
    }

    private boolean isLastOp(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        int required = 0;
        for (String attr : LOCK_ATTRIBUTES) {
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
