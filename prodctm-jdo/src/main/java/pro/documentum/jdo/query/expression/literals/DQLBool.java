package pro.documentum.jdo.query.expression.literals;

import org.datanucleus.query.expression.VariableExpression;

import pro.documentum.jdo.query.IDQLEvaluator;
import pro.documentum.jdo.query.IVariableEvaluator;
import pro.documentum.jdo.query.expression.DQLExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLBool extends DQLLiteral<Boolean> {

    public static final String TRUE = "TRUE";

    public static final String FALSE = "FALSE";

    DQLBool(final Boolean value) {
        super(value, String.valueOf(value).toUpperCase());
    }

    private static DQLBool evaluate(final VariableExpression expression,
            final IDQLEvaluator evaluator) {
        String name = expression.getId();
        if (!TRUE.equalsIgnoreCase(name) && !FALSE.equalsIgnoreCase(name)) {
            return null;
        }
        return getInstance(name.toUpperCase());
    }

    private static DQLBool getInstance(final String value) {
        return new DQLBool(TRUE.equalsIgnoreCase(value));
    }

    public static IVariableEvaluator getVariableEvaluator() {
        return new IVariableEvaluator() {
            @Override
            public DQLExpression evaluate(final VariableExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLBool.evaluate(expression, evaluator);
            }
        };
    }

}
