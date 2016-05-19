package pro.documentum.jdo.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.query.compiler.CompilationComponent;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.OrderExpression;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.store.query.Query;
import org.datanucleus.util.NucleusLogger;

import pro.documentum.jdo.query.expression.DQLAggregate;
import pro.documentum.jdo.query.expression.DQLAny;
import pro.documentum.jdo.query.expression.DQLBoolean;
import pro.documentum.jdo.query.expression.DQLExpression;
import pro.documentum.jdo.query.expression.DQLField;
import pro.documentum.jdo.query.expression.functions.DQLDateToString;
import pro.documentum.jdo.query.expression.functions.DQLLower;
import pro.documentum.jdo.query.expression.functions.DQLUpper;
import pro.documentum.jdo.query.expression.literals.DQLBool;
import pro.documentum.jdo.query.expression.literals.DQLDate;
import pro.documentum.jdo.query.expression.literals.DQLLiteral;
import pro.documentum.jdo.query.expression.literals.DQLString;
import pro.documentum.jdo.query.expression.literals.nulls.DQLNull;

public class JDOQL2DQL extends AbstractDQLEvaluator implements IDQLEvaluator {

    private static final List<IInvokeEvaluator> INVOKE_EVALUATORS;

    static {
        INVOKE_EVALUATORS = new ArrayList<IInvokeEvaluator>();
        INVOKE_EVALUATORS.add(DQLDate.getInvokeEvaluator());
        INVOKE_EVALUATORS.add(DQLAny.getInvokeEvaluator());
        INVOKE_EVALUATORS.add(DQLDateToString.getInvokeEvaluator());
        INVOKE_EVALUATORS.add(DQLUpper.getInvokeEvaluator());
        INVOKE_EVALUATORS.add(DQLLower.getInvokeEvaluator());
    }

    public JDOQL2DQL(final QueryCompilation compilation, final Map params,
            final AbstractClassMetaData cmd, final ExecutionContext ec,
            final Query query) {
        super(compilation, params, cmd, ec, query);
    }

    @Override
    protected void doCompileOrder() {
        // todo: actually in order to save positions of repeating attributes
        // we need to sort by r_object_id, i_position
        StringBuilder orderStr = new StringBuilder();
        Expression[] orderingExpr = getExprOrdering();
        for (int i = 0; i < orderingExpr.length; i++) {
            OrderExpression orderExpr = (OrderExpression) orderingExpr[i];
            orderExpr.evaluate(this);
            DQLExpression dqlExpr = popExpression();
            orderStr.append(dqlExpr.getText());
            String orderDir = orderExpr.getSortOrder();
            if ("descending".equalsIgnoreCase(orderDir)) {
                orderStr.append(" DESC");
            }
            if (i < orderingExpr.length - 1) {
                orderStr.append(",");
            }
        }
        setOrderText(orderStr.toString());
    }

    @Override
    protected void doCompileResult() {
        StringBuilder str = new StringBuilder();
        Expression[] resultExprs = getExprResult();
        int i = 0;
        for (Expression expr : resultExprs) {
            DQLExpression dqlExpression = null;
            if (DQLExpression.isPrimary(expr)) {
                processPrimaryExpression(DQLExpression.asPrimary(expr));
                dqlExpression = popExpression();
                str.append(dqlExpression.getText());
            } else if (DQLExpression.isLiteral(expr)) {
                processLiteral(DQLExpression.asLiteral(expr));
                dqlExpression = popExpression();
                str.append(dqlExpression.getText());
            } else if (DQLExpression.isParameter(expr)) {
                processParameterExpression(DQLExpression.asParameter(expr));
                dqlExpression = popExpression();
                str.append(dqlExpression.getText());
            } else if (DQLExpression.isInvoke(expr)) {
                processInvokeExpression(DQLExpression.asInvoke(expr), str);
            } else {
                NucleusLogger.GENERAL.info("Query result expression " + expr
                        + " not supported via DQL "
                        + "so will be processed in-memory");
                setResultComplete(false);
                break;
            }
            if (i < resultExprs.length - 1) {
                str.append(",");
            }
            i++;
        }
        setResultText(str.toString());
    }

    private void processInvokeExpression(final InvokeExpression invokeExpr,
            final StringBuilder builder) {
        if (invokeExpr.getLeft() != null) {
            return;
        }
        List<Expression> argExprs = invokeExpr.getArguments();
        if (argExprs == null || argExprs.size() != 1) {
            throw new NucleusUserException(
                    "Invalid number of arguments for aggregate expression");
        }

        Expression argExpr = argExprs.get(0);
        if (DQLExpression.isPrimary(argExpr)) {
            processPrimaryExpression(DQLExpression.asPrimary(argExpr));
        } else {
            throw new NucleusUserException("Invocation of static method "
                    + invokeExpr.getOperation() + " with arg of type "
                    + argExpr.getClass().getName()
                    + " not supported in-datastore");
        }

        DQLExpression aggrArgExpr = popExpression();
        if (DQLAggregate.isAggregateExpr(invokeExpr)) {
            DQLExpression aggExpr = DQLAggregate.getInstance(invokeExpr
                    .getOperation(), aggrArgExpr);
            // noinspection ConstantConditions
            builder.append(aggExpr.getText());
        } else {
            throw new NucleusUserException("Invocation of static method "
                    + invokeExpr.getOperation() + " not supported in-datastore");
        }
    }

    protected DQLExpression processAndOrExpression(final Expression expr,
            final Expression.DyadicOperator op) {
        DQLBoolean right = (DQLBoolean) popExpression();
        DQLBoolean left = (DQLBoolean) popExpression();
        DQLBoolean andExpr = DQLBoolean.getInstance(left, right, op);
        if (andExpr != null) {
            return pushExpression(andExpr);
        }
        return null;
    }

    @Override
    protected Object processAndExpression(final Expression expr) {
        DQLExpression andExpr = processAndOrExpression(expr, Expression.OP_AND);
        if (andExpr != null) {
            return andExpr;
        }
        return super.processAndExpression(expr);
    }

    @Override
    protected Object processOrExpression(final Expression expr) {
        DQLExpression orExpr = processAndOrExpression(expr, Expression.OP_OR);
        if (orExpr != null) {
            return orExpr;
        }
        return super.processOrExpression(expr);
    }

    @Override
    protected Object processInvokeExpression(final InvokeExpression invokeExpr) {
        DQLExpression expression;
        for (IInvokeEvaluator evaluator : INVOKE_EVALUATORS) {
            expression = evaluator.evaluate(invokeExpr, this);
            if (expression != null) {
                return pushExpression(expression);
            }
        }
        return super.processInvokeExpression(invokeExpr);
    }

    public DQLExpression processLiteralOrParameter(final Expression expression) {
        if (DQLExpression.isLiteral(expression)) {
            processLiteral(DQLExpression.asLiteral(expression));
        } else if (DQLExpression.isParameter(expression)) {
            processParameterExpression(DQLExpression.asParameter(expression));
        } else {
            return null;
        }
        return popExpression();
    }

    private Object processExpression(final Expression expr,
            final Expression.DyadicOperator op) {
        DQLExpression right = popExpression();
        DQLExpression left = popExpression();
        DQLBoolean dqlExpression = DQLBoolean.getInvariant(left, right);
        if (dqlExpression == null) {
            dqlExpression = DQLBoolean.getInstance(left, right, op);
        }
        if (dqlExpression != null) {
            return pushExpression(dqlExpression);
        }
        return null;
    }

    @Override
    protected Object processEqExpression(final Expression expr) {
        Object result = processExpression(expr, Expression.OP_EQ);
        if (result != null) {
            return result;
        }
        return super.processEqExpression(expr);
    }

    @Override
    protected Object processNoteqExpression(final Expression expr) {
        Object result = processExpression(expr, Expression.OP_NOTEQ);
        if (result != null) {
            return result;
        }
        return super.processNoteqExpression(expr);
    }

    @Override
    protected Object processGtExpression(final Expression expr) {
        Object result = processExpression(expr, Expression.OP_GT);
        if (result != null) {
            return result;
        }
        return super.processGtExpression(expr);
    }

    @Override
    protected Object processLtExpression(final Expression expr) {
        Object result = processExpression(expr, Expression.OP_LT);
        if (result != null) {
            return result;
        }
        return super.processLtExpression(expr);
    }

    @Override
    protected Object processGteqExpression(final Expression expr) {
        Object result = processExpression(expr, Expression.OP_GTEQ);
        if (result != null) {
            return result;
        }
        return super.processGteqExpression(expr);
    }

    @Override
    protected Object processLteqExpression(final Expression expr) {
        Object result = processExpression(expr, Expression.OP_LTEQ);
        if (result != null) {
            return result;
        }
        return super.processLteqExpression(expr);
    }

    @Override
    protected Object processNotExpression(final Expression expr) {
        DQLExpression expression = popExpression();
        if (DQLBoolean.isBooleanExpression(expression)) {
            DQLExpression dqlBooleanExpression = DQLBoolean.getInstance(
                    DQLBoolean.asBooleanExpression(expression),
                    Expression.OP_NOT);
            if (dqlBooleanExpression != null) {
                return pushExpression(dqlBooleanExpression);
            }
        }
        return super.processNotExpression(expr);
    }

    @Override
    protected Object processParameterExpression(final ParameterExpression expr) {
        Object paramValue = getParameterValue(expr);
        // todo: do we need support collections?
        DQLLiteral literal = DQLLiteral.getInstance(paramValue);
        if (literal != null) {
            setPrecompilable(false);
            return pushExpression(literal);
        }

        NucleusLogger.QUERY
                .info("Don't currently support parameter values of type "
                        + paramValue.getClass().getName());

        return super.processParameterExpression(expr);
    }

    @Override
    protected Object processVariableExpression(final VariableExpression expr) {
        String name = expr.getId();
        DQLExpression expression = null;
        if (DQLBool.isBooleanVar(expr)) {
            expression = DQLBool.getInstance(name);
        } else if (DQLString.isLiteralVar(expr)) {
            expression = DQLString.getInstance(name, false);
        } else if (DQLDate.isDateVar(expr)) {
            expression = DQLDate.getInstance(name);
        } else if (DQLNull.isNullVar(expr)) {
            expression = DQLNull.getInstance(name);
        }
        if (expression != null) {
            return pushExpression(expression);
        }
        return super.processVariableExpression(expr);
    }

    @Override
    public Object processPrimaryExpression(final PrimaryExpression expr) {
        Expression left = expr.getLeft();
        if (left != null) {
            return super.processPrimaryExpression(expr);
        }

        if (expr.getId().equals(getCandidateAlias())) {
            DQLField fieldExpr = new DQLField(getCandidateAlias());
            return pushExpression(fieldExpr);
        }

        String fieldName = getFieldNameForPrimary(expr);
        if (fieldName != null) {
            DQLField fieldExpr = new DQLField(getCandidateAlias() + "."
                    + fieldName);
            return pushExpression(fieldExpr);
        }

        if (getCompilcationComponent() == CompilationComponent.FILTER) {
            setFilterComplete(false);
        }

        NucleusLogger.QUERY.debug(">> Primary " + expr
                + " is not stored in this Documentum type,"
                + " so unexecutable in datastore");

        return super.processPrimaryExpression(expr);
    }

    @Override
    protected Object compileOrAndExpression(final Expression expr) {
        try {
            return super.compileOrAndExpression(expr);
        } catch (Exception ex) {
            Object result = transformToInvariant(expr);
            if (result == null) {
                NucleusLogger.QUERY.error("Compilation of filter "
                        + "to be evaluated completely "
                        + "in-datastore was impossible : " + ex.getMessage(),
                        ex);
                throw ex;
            }
            return result;
        }
    }

    private Object transformToInvariant(final Expression expr) {
        Expression parent = expr.getParent();
        if (!DQLExpression.isDyadic(parent)) {
            return null;
        }
        if (parent.getLeft() == null || parent.getRight() == null) {
            return null;
        }
        setFilterComplete(false);
        boolean parity = true;
        while (true) {
            parent = parent.getParent();
            if (parent == null) {
                break;
            }
            if (DQLExpression.isDyadicNot(parent)) {
                parity = !parity;
            }
        }
        return pushExpression(DQLBoolean.getInvariant(parity));
    }

    @Override
    protected Object processLiteral(final Literal expr) {
        Object litValue = expr.getLiteral();
        DQLLiteral literal = DQLLiteral.getInstance(litValue);
        if (literal != null) {
            return pushExpression(literal);
        }
        return super.processLiteral(expr);
    }

}
