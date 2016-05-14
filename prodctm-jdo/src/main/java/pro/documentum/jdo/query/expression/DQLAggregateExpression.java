package pro.documentum.jdo.query.expression;

import org.datanucleus.query.expression.InvokeExpression;

/**
 * Representation of an aggregate (MAX, MIN, AVG, SUM, COUNT) in DQL.
 */
public class DQLAggregateExpression extends DQLExpression {

    public DQLAggregateExpression(final String text) {
        super(text);
    }

    public static DQLAggregateExpression getInstance(
            final String aggregateName, final DQLExpression fieldExpr) {
        StringBuilder builder = new StringBuilder();
        if ("MAX".equalsIgnoreCase(aggregateName)) {
            builder.append("max");
        } else if ("MIN".equalsIgnoreCase(aggregateName)) {
            builder.append("min");
        } else if ("SUM".equalsIgnoreCase(aggregateName)) {
            builder.append("sum");
        } else if ("AVG".equalsIgnoreCase(aggregateName)) {
            builder.append("avg");
        } else if ("COUNT".equalsIgnoreCase(aggregateName)) {
            builder.append("count");
        } else {
            return null;
        }
        builder.append("(").append(fieldExpr.getText()).append(")");
        return new DQLAggregateExpression(builder.toString());
    }

    public static boolean isAggregateExpr(final InvokeExpression invokeExpr) {
        String op = invokeExpr.getOperation();
        return "MAX".equalsIgnoreCase(op) || "MIN".equalsIgnoreCase(op)
                || "SUM".equalsIgnoreCase(op) || "AVG".equalsIgnoreCase(op)
                || "COUNT".equalsIgnoreCase(op);
    }

}
