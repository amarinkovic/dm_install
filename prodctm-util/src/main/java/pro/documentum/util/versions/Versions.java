package pro.documentum.util.versions;

import java.util.Objects;
import java.util.StringTokenizer;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfVersionPolicy;
import com.documentum.fc.common.DfException;
import com.documentum.operations.IDfCheckinOperation;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class Versions {

    private Versions() {
        super();
    }

    private static String getTreeQuery(final IDfSysObject document)
        throws DfException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT r_object_id, i_chronicle_id,");
        queryBuilder.append(" i_antecedent_id, r_version_label FROM ");
        queryBuilder.append(document.getTypeName());
        queryBuilder.append("(ALL) WHERE ");
        queryBuilder.append(" i_chronicle_id='");
        queryBuilder.append(document.getChronicleId().getId());
        queryBuilder.append("'");
        queryBuilder.append(" AND i_position=-1");
        queryBuilder.append(" ENABLE(ROW_BASED)");
        return queryBuilder.toString();
    }

    public static int getNextVersion(final IDfSysObject sysObject,
            final String labels) throws DfException {
        IDfVersionPolicy versionPolicy = sysObject.getVersionPolicy();
        String minorVersion = versionPolicy.getNextMinorLabel();
        String majorVersion = versionPolicy.getNextMajorLabel();
        String branchVersion = versionPolicy.getBranchLabel();
        String sameVersion = versionPolicy.getSameLabel();
        StringTokenizer tokenizer = new StringTokenizer(labels, ",");
        while (tokenizer.hasMoreTokens()) {
            String versionLabel = tokenizer.nextToken();
            if (Objects.equals(majorVersion, versionLabel)) {
                return IDfCheckinOperation.NEXT_MAJOR;
            }
            if (Objects.equals(minorVersion, versionLabel)) {
                return IDfCheckinOperation.NEXT_MINOR;
            }
            if (Objects.equals(branchVersion, versionLabel)) {
                return IDfCheckinOperation.BRANCH_VERSION;
            }
            if (Objects.equals(sameVersion, versionLabel)) {
                return IDfCheckinOperation.SAME_VERSION;
            }
        }
        return IDfCheckinOperation.NEXT_MINOR;
    }

}
