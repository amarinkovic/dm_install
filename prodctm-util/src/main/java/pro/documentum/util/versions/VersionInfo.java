package pro.documentum.util.versions;

import java.util.Objects;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class VersionInfo implements Comparable<VersionInfo> {

    private final Version _version;

    private final String _objectId;

    private final String _chronicleId;

    private final String _antecedentId;

    public VersionInfo(final String versionLabel, final String objectId,
            final String chronicleId, final String antecedentId) {
        _version = new Version(versionLabel);
        _objectId = objectId;
        _chronicleId = chronicleId;
        _antecedentId = antecedentId;
    }

    public static VersionInfo of(final IDfTypedObject row) throws DfException {
        return new VersionInfo(row
                .getString(DfDocbaseConstants.R_VERSION_LABEL), row
                .getString(DfDocbaseConstants.R_OBJECT_ID), row
                .getString(DfDocbaseConstants.I_CHRONICLE_ID), row
                .getString("i_antecedent_id"));
    }

    public static VersionInfo getInitial(final String objectId) {
        return new VersionInfo(Version.INITIAL_VERSION, objectId, objectId,
                DfId.DF_NULLID_STR);
    }

    public Version getVersion() {
        return _version;
    }

    public String getObjectId() {
        return _objectId;
    }

    public String getChronicleId() {
        return _chronicleId;
    }

    public String getAntecedentId() {
        return _antecedentId;
    }

    public VersionInfo version(final String objectId, final int versionPolicy) {
        String label = getVersion().getNextVersion(versionPolicy);
        return new VersionInfo(label, objectId, getChronicleId(), getObjectId());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VersionInfo that = (VersionInfo) o;
        return Objects.equals(_version, that._version)
                && Objects.equals(_objectId, that._objectId)
                && Objects.equals(_chronicleId, that._chronicleId)
                && Objects.equals(_antecedentId, that._antecedentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_version, _objectId, _chronicleId, _antecedentId);
    }

    @Override
    public int compareTo(final VersionInfo o) {
        return new CompareToBuilder().append(_version, o._version).append(
                _objectId, o._objectId).append(_chronicleId, o._chronicleId)
                .append(_antecedentId, o._antecedentId).toComparison();
    }

}
