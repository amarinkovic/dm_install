package pro.documentum.jdo.query.expression.literals;

import org.datanucleus.query.expression.VariableExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLBool extends DQLLiteral<Boolean> {

    DQLBool(final Boolean value) {
        super(value, String.valueOf(value).toUpperCase());
    }

    public static boolean isBooleanExpression(
            final VariableExpression expression) {
        String name = expression.getId();
        return "TRUE".equalsIgnoreCase(name) || "FALSE".equalsIgnoreCase(name);
    }

    public static DQLBool getInstance(final String value) {
        return new DQLBool("TRUE".equalsIgnoreCase(value));
    }

}
