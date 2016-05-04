package pro.documentum.util.versions;

import com.documentum.fc.client.IDfVersionPolicy;
import com.documentum.fc.common.DfUtil;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class Version implements Comparable<Version> {

    public static final String INITIAL_VERSION = "1.0";

    private final String _version;

    public Version(final String version) {
        _version = version;
    }

    public String getVersion() {
        return _version;
    }

    public String getNextVersion(final int policy) {
        switch (policy) {
        case IDfVersionPolicy.DF_SAME_VERSION:
            return _version;
        case IDfVersionPolicy.DF_NEXT_MAJOR:
            return getNextMajorVersion();
        case IDfVersionPolicy.DF_NEXT_MINOR:
            return getNextMinorVersion();
        case IDfVersionPolicy.DF_BRANCH_VERSION:
            return getNextBranchVersion();
        default:
            throw new IllegalArgumentException("Invalid version policy: "
                    + policy);
        }
    }

    public String getNextMajorVersion() {
        int index = _version.indexOf(".");
        String majorStr = _version.substring(0, index);
        int version = Integer.parseInt(majorStr);
        return DfUtil.toString(version + 1) + ".0";
    }

    public String getNextMinorVersion() {
        int index = _version.lastIndexOf(".");
        String minor = _version.substring(index + 1);
        int iver = Integer.parseInt(minor);
        return _version.substring(0, index + 1) + Integer.toString(iver + 1);
    }

    public String getNextBranchVersion() {
        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Version that = (Version) o;

        if (_version != null) {
            if (_version.equals(that._version)) {
                return true;
            }
            return false;
        }
        if (that._version == null) {
            return true;
        }
        return false;

    }

    @Override
    public int hashCode() {
        if (_version != null) {
            return _version.hashCode();
        }
        return 0;
    }

    @Override
    public int compareTo(final Version that) {
        if (that == null) {
            return 1;
        }

        if (equals(that)) {
            return 0;
        }

        if (_version == null) {
            if (that._version == null) {
                return 0;
            }
            return -1;
        }

        if (that._version == null) {
            return 1;
        }

        String[] thisParts = _version.split("\\.");
        String[] thatParts = that._version.split("\\.");

        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart;
            if (i < thisParts.length) {
                thisPart = Integer.parseInt(thisParts[i]);
            } else {
                thisPart = 0;
            }
            int thatPart;
            if (i < thatParts.length) {
                thatPart = Integer.parseInt(thatParts[i]);
            } else {
                thatPart = 0;
            }
            if (thisPart < thatPart) {
                return -1;
            }
            if (thisPart > thatPart) {
                return 1;
            }
        }
        return 0;
    }

}
