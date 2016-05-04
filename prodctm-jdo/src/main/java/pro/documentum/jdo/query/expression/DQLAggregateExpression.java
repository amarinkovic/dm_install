package pro.documentum.jdo.query.expression;

/**
 * Representation of an aggregate (MAX, MIN, AVG, SUM, COUNT) in DQL.
 */
public class DQLAggregateExpression extends DQLExpression {

    public DQLAggregateExpression(final String aggregateName,
            final DQLExpression fieldExpr) {
        if ("MAX".equalsIgnoreCase(aggregateName)) {
            setDqlText("max(" + fieldExpr.getDqlText() + ")");
        } else if ("MIN".equalsIgnoreCase(aggregateName)) {
            setDqlText("min(" + fieldExpr.getDqlText() + ")");
        } else if ("SUM".equalsIgnoreCase(aggregateName)) {
            setDqlText("sum(" + fieldExpr.getDqlText() + ")");
        } else if ("AVG".equalsIgnoreCase(aggregateName)) {
            setDqlText("avg(" + fieldExpr.getDqlText() + ")");
        } else if ("COUNT".equalsIgnoreCase(aggregateName)) {
            setDqlText("count(" + fieldExpr.getDqlText() + ")");
        }
    }

}
