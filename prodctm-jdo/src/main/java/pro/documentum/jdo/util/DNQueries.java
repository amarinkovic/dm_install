package pro.documentum.jdo.util;

import java.util.ArrayList;
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

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import pro.documentum.jdo.query.result.DQLQueryResult;
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
        List<String> result = new ArrayList<String>();
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
        Set<String> selectColumns = new LinkedHashSet<String>();
        Table table = sd.getTable();
        List<Column> columns = table.getColumns();
        for (Column column : columns) {
            selectColumns.addAll(getSelectColumns(column));
        }

        StringBuilder queryBuilder = new StringBuilder(
                selectColumns.size() * 16);
        queryBuilder.append("SELECT ");
        queryBuilder.append(ReservedWords.makeProjection(selectColumns));
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

    public static List executeDqlQuery(final Query query,
            final IDfSession session, final String dqlText,
            final AbstractClassMetaData candidateCmd) {
        boolean processed = false;
        List<IDfCollection> collections = new ArrayList<IDfCollection>();
        try {
            DQLQueryResult result = new DQLQueryResult(query);
            int[] members = query.getFetchPlan().getFetchPlanForClass(
                    candidateCmd).getMemberNumbers();
            IDfCollection collection = new DfQuery(dqlText).execute(session,
                    IDfQuery.DF_EXEC_QUERY);
            collections.add(collection);
            result.addCandidateResult(candidateCmd, collection, members);
            processed = true;
            return result;
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        } finally {
            if (!processed) {
                for (IDfCollection collection : collections) {
                    Queries.close(collection);
                }
            }
        }
    }

}
