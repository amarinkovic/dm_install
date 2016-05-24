package pro.documentum.persistence.common.query.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.InvokeExpression;

import pro.documentum.persistence.common.query.IDQLEvaluator;
import pro.documentum.persistence.common.query.IInvokeEvaluator;
import pro.documentum.persistence.common.query.expression.literals.DQLCollection;
import pro.documentum.persistence.common.query.expression.literals.DQLLiteral;
import pro.documentum.util.queries.bulk.SubIterator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DQLIN extends DQLBoolean {

    public static final String IN = "IN";

    public static final String IN_Q = " IN (";

    public static final String CONTAINS = "contains";

    private DQLIN(final String text) {
        super(text);
    }

    private static DQLExpression evaluate(final InvokeExpression invokeExpr,
            final IDQLEvaluator evaluator) {
        String op = invokeExpr.getOperation();
        if (!CONTAINS.equalsIgnoreCase(op)) {
            return null;
        }
        List<Expression> args = invokeExpr.getArguments();
        if (!hasRequiredArgs(args, 1)) {
            return null;
        }
        Expression arg = args.get(0);
        if (!isPrimary(arg)) {
            return null;
        }
        if (arg.evaluate(evaluator) == null) {
            return null;
        }
        DQLField field = (DQLField) evaluator.popExpression();
        Expression left = invokeExpr.getLeft();
        if (!isLiteralOrParameter(left) && !isVariable(left)) {
            return null;
        }
        if (left.evaluate(evaluator) == null) {
            return null;
        }
        DQLExpression expression = evaluator.popExpression();
        if (DQLCollection.isCollection(expression)) {
            return createINExpression(field, (DQLCollection) expression);
        }
        return null;
    }

    private static DQLExpression createINExpression(final DQLField field,
            final DQLCollection collection) {
        if (!collection.hasNullElements() && !collection.hasNotNullElements()) {
            return null;
        }
        List<DQLBoolean> booleans = new ArrayList<>();
        for (DQLLiteral<?> literal : collection.getNullElements()) {
            booleans.add(DQLBoolean.getInstance(field, literal,
                    Expression.OP_EQ));
        }
        SubIterator<DQLLiteral<?>> sub = new SubIterator<DQLLiteral<?>>(
                collection.getNotNullElements(), 250);
        StringBuilder builder = new StringBuilder();
        builder.append(field.getText()).append(IN_Q);
        int initial = builder.length();
        while (sub.hasNext()) {
            Iterator<DQLLiteral<?>> iter = sub.next().iterator();
            while (iter.hasNext()) {
                DQLLiteral<?> literal = iter.next();
                builder.append(literal.getText());
                if (iter.hasNext()) {
                    builder.append(",");
                } else {
                    builder.append(")");
                }
            }
            booleans.add(new DQLIN(builder.toString()));
            builder.setLength(initial);
        }

        DQLBoolean result = booleans.get(0);
        for (int i = 1, n = booleans.size(); i < n; i++) {
            result = DQLBoolean.getInstance(result, booleans.get(i),
                    Expression.OP_OR);
        }
        return result;
    }

    public static IInvokeEvaluator getInvokeEvaluator() {
        return new IInvokeEvaluator() {
            @Override
            public DQLExpression evaluate(final InvokeExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLIN.evaluate(expression, evaluator);
            }
        };
    }

}
