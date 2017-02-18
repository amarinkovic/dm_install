package pro.documentum.persistence.common.query;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.query.compiler.CompilationComponent;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.query.evaluator.AbstractExpressionEvaluator;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.store.query.Query;
import org.datanucleus.store.schema.table.Table;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.StringUtils;

import pro.documentum.persistence.common.query.expression.DQLExpression;
import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNQueries;
import pro.documentum.persistence.common.util.DNRelation;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractDQLEvaluator<R, T extends Query<?> & IDocumentumQuery<R>>
        extends AbstractExpressionEvaluator {

    private final Deque<DQLExpression> _exprs = new ArrayDeque<>();

    private final T _query;

    private final AbstractDQLEvaluator<?, ?> _parentMapper;

    private final Map<?, ?> _params;

    private String _filterText;

    private String _orderText;

    private String _resultText;

    private boolean _filterComplete = true;

    private boolean _resultComplete = true;

    private boolean _orderComplete = true;

    private boolean _precompilable = true;

    private CompilationComponent _compilationComponent;

    private int _positionalParamNumber = -1;

    public AbstractDQLEvaluator(final T query,
            final AbstractDQLEvaluator<?, ?> parentMapper,
            final Map<?, ?> params) {
        _params = params;
        _query = query;
        _parentMapper = parentMapper;
    }

    public void compile(final DQLQueryCompilation dqlCompilation) {
        compileFilter();
        compileResult();
        compileOrder();

        dqlCompilation.setPrecompilable(isPrecompilable());
        dqlCompilation.setFilterComplete(isFilterComplete());
        dqlCompilation.setResultComplete(isResultComplete());
        dqlCompilation.setOrderComplete(isOrderComplete());

        Long rangeFrom = null;
        Long rangeTo = null;
        if (isFilterComplete() && isOrderComplete()) {
            if (getRangeFromIncl() > 0) {
                rangeFrom = getRangeFromIncl();
            } else {
                rangeFrom = null;
            }
            if (getRangeToExcl() != Long.MAX_VALUE) {
                rangeTo = getRangeToExcl();
            } else {
                rangeTo = null;
            }
            if (rangeFrom != null || rangeTo != null) {
                dqlCompilation.setRangeComplete(true);
            }
        }

        String resultText = null;
        if (isResultComplete()) {
            resultText = getResultText();
        }
        String dqlText = DNQueries.getDqlTextForQuery(_query, getFilterText(),
                resultText, getOrderText(), rangeFrom, rangeTo);
        dqlCompilation.setDqlText(dqlText);
    }

    protected void compileFilter() {
        if (_query.getCompilation().getExprFilter() == null) {
            return;
        }

        setCompilationComponent(CompilationComponent.FILTER);

        try {
            _query.getCompilation().getExprFilter().evaluate(this);
            DQLExpression dqlExpr = popExpression();
            setFilterText(dqlExpr.getText());
        } catch (Exception e) {
            NucleusLogger.QUERY.error("Compilation of filter "
                    + "to be evaluated completely "
                    + "in-datastore was impossible : " + e.getMessage(), e);
            setFilterComplete(false);
        }

        setCompilationComponent(null);
    }

    protected void compileOrder() {
        if (getExprOrdering() == null) {
            return;
        }

        setCompilationComponent(CompilationComponent.ORDERING);

        try {
            doCompileOrder();
        } catch (Exception e) {
            NucleusLogger.QUERY.error("Compilation of ordering "
                    + "to be evaluated completely in-datastore "
                    + "was impossible : " + e.getMessage(), e);
            setOrderComplete(false);
        }

        setCompilationComponent(null);
    }

    protected Expression[] getExprOrdering() {
        return _query.getCompilation().getExprOrdering();
    }

    protected void compileResult() {
        if (getExprResult() == null) {
            return;
        }

        setCompilationComponent(CompilationComponent.RESULT);
        setResultComplete(true);

        try {
            doCompileResult();
        } catch (Exception e) {
            NucleusLogger.GENERAL.info("Query result clause "
                    + StringUtils.objectArrayToString(getExprResult())
                    + " not totally supported via DQL "
                    + "so will be processed in-memory");
            setResultComplete(false);
        }

        setCompilationComponent(null);
    }

    protected Expression[] getExprResult() {
        return _query.getCompilation().getExprResult();
    }

    protected abstract void doCompileOrder();

    protected abstract void doCompileResult();

    protected Object getParameterValue(final ParameterExpression expr) {
        Object paramValue = null;
        if (_params == null || _params.isEmpty()) {
            setPrecompilable(false);
            throw new NucleusException("Parameter " + expr
                    + " is not currently set, so cannot "
                    + "complete the _queryCompilation");
        }

        boolean paramValueSet = false;
        if (_params.containsKey(expr.getId())) {
            paramValue = _params.get(expr.getId());
            paramValueSet = true;
        } else if (_params.containsKey(expr.getId())) {
            paramValue = _params.get(expr.getId());
            paramValueSet = true;
        } else {
            int position = _positionalParamNumber;
            if (_positionalParamNumber < 0) {
                position = 0;
            }
            if (_params.containsKey(position)) {
                paramValue = _params.get(position);
                paramValueSet = true;
                _positionalParamNumber = position + 1;
            }
        }

        if (!paramValueSet) {
            setPrecompilable(false);
            throw new NucleusException("Parameter " + expr
                    + " is not currently set, so cannot"
                    + " complete the _queryCompilation");
        }
        return paramValue;
    }

    protected AbstractClassMetaData getMetadataFromAlias(final String alias) {
        AbstractDQLEvaluator<?, ?> mapper = this;
        while (mapper != null) {
            if (alias.equals(mapper.getCandidateAlias())) {
                return mapper.getCandidateMetaData();
            }
            mapper = mapper._parentMapper;
        }
        return null;
    }

    protected Table getTable(final AbstractClassMetaData cmd) {
        return DNMetaData.getTable(_query.getExecutionContext(), cmd);
    }

    protected String getFieldNameForPrimary(final PrimaryExpression expr) {
        List<String> tuples = expr.getTuples();
        if (tuples == null || tuples.isEmpty()) {
            return null;
        }

        AbstractClassMetaData cmd = _query.getCandidateMetaData();
        Table table = getTable(cmd);
        AbstractMemberMetaData embMmd = null;

        List<AbstractMemberMetaData> embMmds = new ArrayList<>();
        ClassLoaderResolver clr = getClassLoaderResolver();
        for (int i = 0, n = tuples.size(); i < n; i++) {
            boolean hasNext = i < n - 1;
            String name = tuples.get(i);
            if (i == 0) {
                AbstractClassMetaData alias = getMetadataFromAlias(name);
                if (alias != null) {
                    cmd = alias;
                    table = getTable(cmd);
                    continue;
                }
            }

            AbstractMemberMetaData mmd = cmd.getMetaDataForMember(name);
            RelationType relationType;
            try {
                relationType = mmd.getRelationType(getClassLoaderResolver());
            } catch (Exception ex) {
                continue;
            }
            if (DNRelation.isNone(relationType)) {
                if (hasNext) {
                    throw new NucleusUserException("Query has reference to "
                            + StringUtils.collectionToString(tuples) + " yet "
                            + name + " is a non-relation field!");
                }
                if (embMmd != null) {
                    embMmds.add(mmd);
                    return DNMetaData.getFirstEmbeddedColumn(table, embMmds);
                }
                return DNMetaData.getFirstColumn(table, mmd);
            }

            AbstractMemberMetaData emmd = null;
            if (!embMmds.isEmpty()) {
                emmd = embMmds.get(embMmds.size() - 1);
            }

            boolean embedded = MetaDataUtils.getInstance().isMemberEmbedded(
                    getMetaDataManager(), clr, mmd, relationType, emmd);

            if (embedded) {
                if (RelationType.isRelationSingleValued(relationType)) {
                    cmd = getMetaDataManager().getMetaDataForClass(
                            mmd.getType(), getClassLoaderResolver());
                    if (embMmd != null) {
                        embMmd = embMmd.getEmbeddedMetaData()
                                .getMemberMetaData()[mmd
                                .getAbsoluteFieldNumber()];
                    } else {
                        embMmd = mmd;
                    }
                    embMmds.add(embMmd);
                } else if (RelationType.isRelationMultiValued(relationType)) {
                    throw new NucleusUserException(
                            "Do not support the querying of embedded collection/map/array fields : "
                                    + mmd.getFullFieldName());
                }
            } else {
                embMmds.clear();
                if (relationType == RelationType.ONE_TO_MANY_UNI
                        || relationType == RelationType.ONE_TO_MANY_BI
                        || relationType == RelationType.MANY_TO_ONE_UNI
                        || relationType == RelationType.MANY_TO_ONE_BI) {
                    if (!hasNext) {
                        return name;
                    }
                    throw new NucleusUserException(
                            "Do not support query joining to related object at "
                                    + mmd.getFullFieldName() + " in "
                                    + StringUtils.collectionToString(tuples));
                }

                if (getCompilcationComponent() == CompilationComponent.FILTER) {
                    setFilterComplete(false);
                }

                NucleusLogger.QUERY.debug("Query has reference to "
                        + StringUtils.collectionToString(tuples) + " and "
                        + mmd.getFullFieldName()
                        + " is not persisted into this object,"
                        + " so unexecutable in the datastore");
                return null;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public T getSubquery(final String variableName) {
        QueryCompilation compilation = _query.getCompilation();
        Query.SubqueryDefinition subqueryDefinition = _query
                .getSubqueryForVariable(variableName);
        if (subqueryDefinition == null) {
            return null;
        }
        T query = (T) subqueryDefinition.getQuery();
        if (query == null) {
            return null;
        }
        QueryCompilation subQueryCompilation = compilation
                .getCompilationForSubquery(variableName);
        query.setCompilation(subQueryCompilation);
        query.getQueryHelper().setParentMapper(this);
        return query;
    }

    public final DQLExpression popExpression() {
        return _exprs.pop();
    }

    protected final DQLExpression pushExpression(
            final DQLExpression dqlExpression) {
        _exprs.push(dqlExpression);
        return dqlExpression;
    }

    protected String getOrderText() {
        return _orderText;
    }

    protected void setOrderText(final String orderText) {
        _orderText = orderText;
    }

    protected String getFilterText() {
        return _filterText;
    }

    protected void setFilterText(final String filterText) {
        _filterText = filterText;
    }

    protected String getResultText() {
        return _resultText;
    }

    protected void setResultText(final String resultText) {
        _resultText = resultText;
    }

    protected boolean isOrderComplete() {
        return _orderComplete;
    }

    protected void setOrderComplete(final boolean orderComplete) {
        _orderComplete = orderComplete;
    }

    protected boolean isResultComplete() {
        return _resultComplete;
    }

    protected void setResultComplete(final boolean resultComplete) {
        _resultComplete = resultComplete;
    }

    protected boolean isFilterComplete() {
        return _filterComplete;
    }

    protected void setFilterComplete(final boolean filterComplete) {
        _filterComplete = filterComplete;
    }

    protected boolean isPrecompilable() {
        return _precompilable;
    }

    protected void setPrecompilable(final boolean precompilable) {
        _precompilable = precompilable;
    }

    protected CompilationComponent getCompilcationComponent() {
        return _compilationComponent;
    }

    protected void setCompilationComponent(
            final CompilationComponent compilationComponent) {
        _compilationComponent = compilationComponent;
    }

    protected long getRangeFromIncl() {
        return _query.getRangeFromIncl();
    }

    protected long getRangeToExcl() {
        return _query.getRangeToExcl();
    }

    protected ClassLoaderResolver getClassLoaderResolver() {
        return _query.getExecutionContext().getClassLoaderResolver();
    }

    protected String getCandidateAlias() {
        return _query.getCandidateAlias();
    }

    protected AbstractClassMetaData getCandidateMetaData() {
        return _query.getCandidateMetaData();
    }

    protected MetaDataManager getMetaDataManager() {
        return _query.getExecutionContext().getMetaDataManager();
    }

}
