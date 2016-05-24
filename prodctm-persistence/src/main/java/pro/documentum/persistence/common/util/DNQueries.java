package pro.documentum.persistence.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.store.StoreData;
import org.datanucleus.store.query.Query;
import org.datanucleus.store.schema.table.Column;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.query.result.DQLQueryResult;
import pro.documentum.util.queries.DfIterator;
import pro.documentum.util.queries.Queries;
import pro.documentum.util.queries.ReservedWords;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNQueries {

    private DNQueries() {
        super();
    }

    public static List<String> getSelectColumns(final Column column) {
        List<String> result = new ArrayList<>();
        MemberColumnMapping mcm = column.getMemberColumnMapping();
        if (mcm == null) {
            result.add(column.getName());
            return result;
        }
        AbstractMemberMetaData mmd = mcm.getMemberMetaData();
        if (!mmd.isDefaultFetchGroup() && !mmd.isSerialized()) {
            return result;
        }
        ColumnMetaData[] columnMetaDatum = null;
        if (mmd.hasContainer()) {
            ElementMetaData emd = mmd.getElementMetaData();
            if (emd != null) {
                ColumnMetaData[] data = emd.getColumnMetaData();
                if (data != null && data.length > 0) {
                    columnMetaDatum = data;
                }
            }
        }
        if (columnMetaDatum == null) {
            columnMetaDatum = mmd.getColumnMetaData();
        }
        for (ColumnMetaData c : columnMetaDatum) {
            result.add(c.getName());
        }
        return result;
    }

    public static String getDqlTextForQuery(final ExecutionContext ec,
            final AbstractClassMetaData cmd, final String candidateAlias,
            final boolean subclasses, final String filterText,
            final String resultText, final String orderText,
            final Long rangeFromIncl, final Long rangeToExcl) {

        StoreData sd = DNMetaData.getStoreData(ec, cmd);
        Table table = sd.getTable();

        String projection = resultText;
        if (StringUtils.isBlank(projection)) {
            Set<String> selectColumns = new LinkedHashSet<>();
            List<Column> columns = table.getColumns();
            for (Column column : columns) {
                selectColumns.addAll(getSelectColumns(column));
            }
            projection = ReservedWords.makeProjection(selectColumns);
        }

        StringBuilder queryBuilder = new StringBuilder(projection.length());
        queryBuilder.append("SELECT ");
        queryBuilder.append(projection);
        queryBuilder.append(" FROM ");
        if (StringUtils.isNotBlank(table.getSchemaName())) {
            queryBuilder.append(table.getSchemaName()).append(".");
        }
        queryBuilder.append(table.getName());
        if (StringUtils.isNotBlank(candidateAlias)) {
            queryBuilder.append(" ").append(candidateAlias);
        }

        // Add any WHERE clause
        if (filterText != null) {
            queryBuilder.append(" WHERE ");
            queryBuilder.append(filterText);
        }

        // Ordering
        if (orderText != null) {
            queryBuilder.append(" ORDER BY ").append(orderText);
        }

        return queryBuilder.toString();
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    public static List<?> executeDqlQuery(final Query<?> query,
            final IDfSession session, final String dqlText,
            final AbstractClassMetaData candidateCmd) {
        boolean processed = false;
        List<DfIterator> collections = new ArrayList<>();
        try {
            DQLQueryResult<?> result = new DQLQueryResult(query);
            int[] members = query.getFetchPlan()
                    .getFetchPlanForClass(candidateCmd).getMemberNumbers();
            DfIterator cursor = Queries.execute(session, dqlText);
            members = getPresentMembers(members, candidateCmd, cursor);
            collections.add(cursor);
            result.addCandidateResult(candidateCmd, cursor, members);
            processed = true;
            return result;
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        } finally {
            if (!processed) {
                for (DfIterator collection : collections) {
                    Queries.close(collection);
                }
            }
        }
    }

    private static int[] getPresentMembers(final int[] members,
            final AbstractClassMetaData cmd, final DfIterator cursor)
        throws DfException {
        int[] result = new int[members.length];
        int i = 0;
        outer: for (int position : members) {
            AbstractMemberMetaData mmd = cmd
                    .getMetaDataForManagedMemberAtAbsolutePosition(position);
            for (ColumnMetaData md : mmd.getColumnMetaData()) {
                String column = md.getName();
                if (!cursor.hasAttr(column)) {
                    continue outer;
                }
            }
            result[i] = position;
            i++;
        }
        return Arrays.copyOf(result, i);
    }

}
