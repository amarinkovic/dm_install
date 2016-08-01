package pro.documentum.util.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfUtil;

import pro.documentum.util.logger.Logger;
import pro.documentum.util.queries.keys.CompositeKey;
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
            Logger.error(ex);
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

    public static String createInClause(final Collection<String> values) {
        return createInClause(DfDocbaseConstants.R_OBJECT_ID, 250, values);
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

    public static String createClause(final CompositeKey key) {
        Map<String, Object> mapping = key.getMapping();
        if (mapping.isEmpty()) {
            throw new IllegalArgumentException("mapping is blank");
        }
        StringBuilder stringBuilder = new StringBuilder(32 * mapping.size());
        for (String attrName : mapping.keySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" AND ");
            }
            stringBuilder.append(attrName);
            Object value = mapping.get(attrName);
            if (value == null) {
                stringBuilder.append(" IS NULL");
                continue;
            }
            stringBuilder.append("=");
            if (value instanceof String) {
                stringBuilder.append("'");
                stringBuilder.append(DfUtil.escapeQuotedString((String) value));
                stringBuilder.append("'");
                continue;
            }
            if (value instanceof Number) {
                stringBuilder.append(String.valueOf(value));
                continue;
            }
            throw new IllegalArgumentException(
                    "Currently only strings and numbers are supported");
        }
        return stringBuilder.toString();
    }

    public static <T extends CompositeKey> String createClause(
            final Collection<T> keys) {
        StringBuilder stringBuilder = new StringBuilder(keys.size() * 32);
        stringBuilder.append("(");
        for (Iterator<T> iter = keys.iterator(); iter.hasNext();) {
            stringBuilder.append("(");
            stringBuilder.append(createClause(iter.next()));
            stringBuilder.append(")");
            if (iter.hasNext()) {
                stringBuilder.append(" OR ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public static <T extends CompositeKey> String createClause(
            final String attrName, final Collection<T> keys) {
        Collection<String> values = new ArrayList<>();
        boolean quoted = false;
        for (CompositeKey key : keys) {
            Map<String, Object> mapping = key.getMapping();
            Object value = mapping.get(attrName);
            if (value instanceof String) {
                values.add((String) value);
                quoted = true;
                continue;
            }
            if (value instanceof Number) {
                values.add(String.valueOf(value));
                continue;
            }
            throw new IllegalArgumentException(
                    "Currently only strings and numbers are supported");
        }
        return createInClause(attrName, 250, values, quoted);
    }

}
