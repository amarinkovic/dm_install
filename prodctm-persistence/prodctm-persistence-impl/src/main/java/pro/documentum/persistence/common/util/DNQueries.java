package pro.documentum.persistence.common.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.query.Query;
import org.datanucleus.store.schema.table.Column;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.query.IDocumentumQuery;
import pro.documentum.persistence.common.query.result.DQLQueryResult;
import pro.documentum.persistence.common.query.result.IResultObjectFactory;
import pro.documentum.persistence.common.query.result.PersistentObjectFactoryImpl;
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

    public static <R, T extends Query<?> & IDocumentumQuery<R>> String getDqlTextForQuery(
            final T query, final String filterText, final String resultText,
            final String orderText, final Long rangeFromIncl,
            final Long rangeToExcl) {

        ExecutionContext context = query.getExecutionContext();
        Table table = DNMetaData
                .getTable(context, query.getCandidateMetaData());

        String projection = resultText;
        if (StringUtils.isBlank(projection)) {
            Set<String> selectColumns = new LinkedHashSet<>();
            List<Column> columns = table.getColumns();
            for (Column column : columns) {
                selectColumns.addAll(DNMetaData.getSelectColumns(context,
                        column, true));
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
        if (StringUtils.isNotBlank(query.getCandidateAlias())) {
            queryBuilder.append(" ").append(query.getCandidateAlias());
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

    public static <R, T extends Query<?> & IDocumentumQuery<R>> List<R> executeDqlQuery(
            final T query, final IDfSession session, final String dqlText) {
        boolean processed = false;
        List<DfIterator> collections = new ArrayList<>();
        try {
            DQLQueryResult<R, T> result = new DQLQueryResult<>(query);
            DfIterator cursor = Queries.execute(session, dqlText);
            collections.add(cursor);
            result.addCandidateResult(cursor, getObjectFactory(query, cursor));
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

    private static <R, T extends Query<?> & IDocumentumQuery<R>> IResultObjectFactory<R> getObjectFactory(
            final T query, final DfIterator cursor) throws DfException {
        Class<?> resultClass = query.getResultClass();
        Class<?> candidateClass = query.getCandidateClass();
        if (query.getResult() != null
                || (resultClass != null && resultClass != candidateClass)) {
            return null;
        }
        AbstractClassMetaData candidateCmd = query.getCandidateMetaData();
        int[] members = query.getFetchPlan().getFetchPlanForClass(candidateCmd)
                .getMemberNumbers();
        members = DNFields.getPresentMembers(members, candidateCmd, cursor);
        return new PersistentObjectFactoryImpl<>(query.getCandidateMetaData(),
                members, query.getIgnoreCache());
    }

}
