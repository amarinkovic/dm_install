package pro.documentum.util.versions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfVersionPolicy;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;
import com.documentum.operations.IDfCheckinOperation;

import pro.documentum.util.ids.DfIdUtil;
import pro.documentum.util.queries.DfIterator;
import pro.documentum.util.queries.Queries;

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
        queryBuilder.append("(DELETED) WHERE ");
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

    public static List<IDfSysObject> getDocumentsTree(
            final IDfSysObject document) throws DfException {
        final List<IDfSysObject> documents = new ArrayList<IDfSysObject>();
        final IDfSession session = document.getObjectSession();
        for (Map.Entry<Version, String> entry : getVersionTree(document)
                .entrySet()) {
            IDfSysObject object = (IDfSysObject) session.getObject(DfIdUtil
                    .getId(entry.getValue()));
            documents.add(object);
        }
        return documents;
    }

    public static SortedMap<Version, String> getVersionTree(
            final IDfSysObject document) throws DfException {
        SortedMap<Version, String> result = new TreeMap<>();
        String query = getTreeQuery(document);
        try (DfIterator iterator = Queries
                .execute(document.getSession(), query)) {
            while (iterator.hasNext()) {
                IDfTypedObject object = iterator.next();
                String version = object
                        .getString(DfDocbaseConstants.R_VERSION_LABEL);
                String objectId = object
                        .getString(DfDocbaseConstants.R_OBJECT_ID);
                result.put(new Version(version), objectId);
            }
        }
        return result;
    }

}
