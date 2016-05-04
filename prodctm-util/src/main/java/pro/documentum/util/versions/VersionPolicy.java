package pro.documentum.util.versions;

import com.documentum.fc.client.IDfVersionPolicy;
import com.documentum.fc.common.DfException;
import com.documentum.operations.IDfCheckinOperation;

public enum VersionPolicy {

    NOTSET(IDfCheckinOperation.VERSION_NOT_SET, ""), MAJOR(
            IDfCheckinOperation.NEXT_MAJOR, "major"), MINOR(
            IDfCheckinOperation.NEXT_MINOR, "minor"), SAME(
            IDfCheckinOperation.SAME_VERSION, "same"), BRANCH(
            IDfCheckinOperation.BRANCH_VERSION, "branch");

    public static final String VERSION_POLICY = "versionPolicy";

    private final int _policy;

    private final String _description;

    VersionPolicy(final int policy, final String description) {
        _policy = policy;
        _description = description;
    }

    public static VersionPolicy getPolicyByDescription(final String description) {
        for (VersionPolicy obj : VersionPolicy.values()) {
            if (obj._description.equalsIgnoreCase(description)) {
                return obj;
            }
        }
        return NOTSET;
    }

    public static String getNextVersionLabel(final String description,
            final IDfVersionPolicy policy) throws DfException {
        int policyCode = getPolicyByDescription(description).getPolicy();

        String result;

        switch (policyCode) {
        case IDfVersionPolicy.DF_NEXT_MAJOR:
            result = policy.getNextMajorLabel();
            break;
        case IDfVersionPolicy.DF_NEXT_MINOR:
            result = policy.getNextMinorLabel();
            break;
        case IDfVersionPolicy.DF_SAME_VERSION:
            result = policy.getSameLabel();
            break;
        case IDfVersionPolicy.DF_BRANCH_VERSION:
            result = policy.getBranchLabel();
            break;
        default:
            result = null;
        }
        return result;
    }

    public int getPolicy() {
        return _policy;
    }

    public String getDescription() {
        return _description;
    }

}
