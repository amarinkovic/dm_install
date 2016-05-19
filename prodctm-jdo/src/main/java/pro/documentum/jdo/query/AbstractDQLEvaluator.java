package pro.documentum.jdo.query;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
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

import pro.documentum.jdo.query.expression.DQLExpression;
import pro.documentum.jdo.util.DNMetaData;
import pro.documentum.jdo.util.DNQueries;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractDQLEvaluator extends AbstractExpressionEvaluator {

    private String _filterText;

    private String _orderText;

    private String _resultText;

    private boolean _filterComplete = true;

    private boolean _resultComplete = true;

    private boolean _orderComplete = true;

    private boolean _precompilable = true;

    private CompilationComponent _compilationComponent;

    private final Deque<DQLExpression> _exprs = new ArrayDeque<DQLExpression>();

    private final Query _query;

    private final ExecutionContext _executionContext;

    private final AbstractClassMetaData _classMetaData;

    private final QueryCompilation _queryCompilation;

    private final Map _params;

    private int _positionalParamNumber = -1;

    public AbstractDQLEvaluator(final QueryCompilation compilation,
            final Map params, final AbstractClassMetaData classMetaData,
            final ExecutionContext executionContext, final Query query) {
        _queryCompilation = compilation;
        _params = params;
        _executionContext = executionContext;
        _classMetaData = classMetaData;
        _query = query;
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
        String dqlText = DNQueries
                .getDqlTextForQuery(_executionContext, _classMetaData,
                        getCandidateAlias(), isSubclasses(), getFilterText(),
                        resultText, getOrderText(), rangeFrom, rangeTo);
        dqlCompilation.setDqlText(dqlText);
    }

    protected void compileFilter() {
        if (_queryCompilation.getExprFilter() == null) {
            return;
        }

        setCompilationComponent(CompilationComponent.FILTER);

        try {
            _queryCompilation.getExprFilter().evaluate(this);
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
        return _queryCompilation.getExprOrdering();
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
        return _queryCompilation.getExprResult();
    }

    protected abstract void doCompileOrder();

    protected abstract void doCompileResult();

    protected Object getParameterValue(final ParameterExpression expr) {
        Object paramValue = null;
        if (_params == null || _params.isEmpty()) {
            setPrecompilable(false);
            throw new NucleusException(
                    "Parameter "
                            + expr
                            + " is not currently set, so cannot complete the _queryCompilation");
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
            throw new NucleusException(
                    "Parameter "
                            + expr
                            + " is not currently set, so cannot complete the _queryCompilation");
        }
        return paramValue;
    }

    protected String getFieldNameForPrimary(final PrimaryExpression expr) {
        List<String> tuples = expr.getTuples();
        if (tuples == null || tuples.isEmpty()) {
            return null;
        }

        AbstractClassMetaData cmd = _classMetaData;
        Table table = DNMetaData.getStoreData(_executionContext, cmd)
                .getTable();
        AbstractMemberMetaData embMmd = null;

        List<AbstractMemberMetaData> embMmds = new ArrayList<AbstractMemberMetaData>();
        boolean firstTuple = true;
        Iterator<String> iter = tuples.iterator();
        ClassLoaderResolver clr = getClassLoaderResolver();
        while (iter.hasNext()) {
            String name = iter.next();
            if (firstTuple && name.equals(getCandidateAlias())) {
                cmd = _classMetaData;
                continue;
            }

            AbstractMemberMetaData mmd = cmd.getMetaDataForMember(name);
            RelationType relationType = mmd
                    .getRelationType(getClassLoaderResolver());
            if (relationType == RelationType.NONE) {
                if (iter.hasNext()) {
                    throw new NucleusUserException("Query has reference to "
                            + StringUtils.collectionToString(tuples) + " yet "
                            + name + " is a non-relation field!");
                }
                if (embMmd != null) {
                    embMmds.add(mmd);
                    return table.getMemberColumnMappingForEmbeddedMember(
                            embMmds).getColumn(0).getName();
                }
                return table.getMemberColumnMappingForMember(mmd).getColumn(0)
                        .getName();
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
                    if (!iter.hasNext()) {
                        return name;
                    }
                    throw new NucleusUserException(
                            "Do not support _query joining to related object at "
                                    + mmd.getFullFieldName() + " in "
                                    + StringUtils.collectionToString(tuples));
                }

                if (getCompilcationComponent() == CompilationComponent.FILTER) {
                    setFilterComplete(false);
                }

                NucleusLogger.QUERY
                        .debug("Query has reference to "
                                + StringUtils.collectionToString(tuples)
                                + " and "
                                + mmd.getFullFieldName()
                                + " is not persisted into this object, so unexecutable in the datastore");
                return null;
            }
            firstTuple = false;
        }

        return null;
    }

    public final DQLExpression popExpression() {
        return _exprs.pop();
    }

    protected final DQLExpression pushExpression(
            final DQLExpression dqlExpression) {
        _exprs.push(dqlExpression);
        return dqlExpression;
    }

    protected void setFilterText(final String filterText) {
        _filterText = filterText;
    }

    protected void setOrderText(final String orderText) {
        _orderText = orderText;
    }

    protected void setResultText(final String resultText) {
        _resultText = resultText;
    }

    protected void setFilterComplete(final boolean filterComplete) {
        _filterComplete = filterComplete;
    }

    protected void setResultComplete(final boolean resultComplete) {
        _resultComplete = resultComplete;
    }

    protected void setOrderComplete(final boolean orderComplete) {
        _orderComplete = orderComplete;
    }

    protected void setPrecompilable(final boolean precompilable) {
        _precompilable = precompilable;
    }

    protected String getOrderText() {
        return _orderText;
    }

    protected String getFilterText() {
        return _filterText;
    }

    protected String getResultText() {
        return _resultText;
    }

    protected boolean isOrderComplete() {
        return _orderComplete;
    }

    protected boolean isResultComplete() {
        return _resultComplete;
    }

    protected boolean isFilterComplete() {
        return _filterComplete;
    }

    protected boolean isPrecompilable() {
        return _precompilable;
    }

    protected CompilationComponent getCompilcationComponent() {
        return _compilationComponent;
    }

    protected void setCompilationComponent(
            final CompilationComponent compilationComponent) {
        _compilationComponent = compilationComponent;
    }

    protected boolean isSubclasses() {
        return _query.isSubclasses();
    }

    protected long getRangeFromIncl() {
        return _query.getRangeFromIncl();
    }

    protected long getRangeToExcl() {
        return _query.getRangeToExcl();
    }

    protected ClassLoaderResolver getClassLoaderResolver() {
        return _executionContext.getClassLoaderResolver();
    }

    protected String getCandidateAlias() {
        return _queryCompilation.getCandidateAlias();
    }

    protected MetaDataManager getMetaDataManager() {
        return _executionContext.getMetaDataManager();
    }

}
