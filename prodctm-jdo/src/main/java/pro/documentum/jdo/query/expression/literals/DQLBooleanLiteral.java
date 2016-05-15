package pro.documentum.jdo.query.expression.literals;

import org.datanucleus.query.expression.VariableExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLBooleanLiteral extends DQLLiteral<Boolean> {

    DQLBooleanLiteral(final Boolean value) {
        super(value, String.valueOf(value).toUpperCase());
    }

    public static boolean isBooleanExpression(
            final VariableExpression expression) {
        String name = expression.getId();
        return "TRUE".equalsIgnoreCase(name) || "FALSE".equalsIgnoreCase(name);
    }

    public static DQLBooleanLiteral getInstance(final String value) {
        return new DQLBooleanLiteral("TRUE".equalsIgnoreCase(value));
    }

}
