package pro.documentum.util.queries;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfUtil;

import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Queries {

    private Queries() {
        super();
    }

    public static void close(final DfIterator collection) {
        collection.close();
    }

    public static void close(final IDfCollection collection) {
        if (collection == null) {
            return;
        }
        if (collection.getState() == IDfCollection.DF_CLOSED_STATE) {
            return;
        }
        try {
            collection.close();
        } catch (DfException ex) {
            // ignore
        }
    }

    public static DfIterator execute(final IDfSession session,
            final String query) throws DfException {
        return execute(session, query, IDfQuery.DF_EXEC_QUERY);
    }

    public static DfIterator execute(final IDfSession session,
            final String query, final int queryType) throws DfException {
        IDfQuery dfQuery = Sessions.getClientX().getQuery();
        dfQuery.setDQL(query);
        return new DfIterator(dfQuery.execute(session, queryType));
    }

    public static String createInClause(final String attrName,
            final Collection<String> values) {
        return createInClause(attrName, 250, values);
    }

    public static String createInClause(final String attrName,
            final int maxItems, final Collection<String> values) {
        return createInClause(attrName, maxItems, values, true);
    }

    public static String createInClause(final String attrName,
            final int maxItems, final Collection<String> values,
            final boolean quoted) {
        if (StringUtils.isBlank(attrName)) {
            throw new IllegalArgumentException("attrname is blank");
        }
        if (values == null || values.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(values.size() * 16);
        stringBuilder.append("( ").append(attrName).append(" IN (");
        Collection<String> resultValues = new HashSet<>(values);
        int iterations = 0;
        for (Iterator<String> iter = resultValues.iterator(); iter.hasNext();) {
            if (quoted) {
                stringBuilder.append("'");
                stringBuilder.append(DfUtil.escapeQuotedString(iter.next()));
                stringBuilder.append("'");
            } else {
                stringBuilder.append(iter.next());
            }
            if (iter.hasNext()) {
                if (iterations > 0 && 0 == iterations % maxItems) {
                    stringBuilder.append(") OR ").append(attrName)
                            .append(" IN (");
                } else {
                    stringBuilder.append(", ");
                }
            }
            iterations++;
        }
        stringBuilder.append("))");
        return stringBuilder.toString();
    }

}
