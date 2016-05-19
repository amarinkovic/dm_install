package pro.documentum.jdo.query.expression.literals;

import org.datanucleus.query.expression.VariableExpression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLBool extends DQLLiteral<Boolean> {

    public static final String TRUE = "TRUE";

    public static final String FALSE = "FALSE";

    DQLBool(final Boolean value) {
        super(value, String.valueOf(value).toUpperCase());
    }

    public static boolean isBooleanVar(final VariableExpression expression) {
        String name = expression.getId();
        return TRUE.equalsIgnoreCase(name) || FALSE.equalsIgnoreCase(name);
    }

    public static DQLBool getInstance(final String value) {
        return new DQLBool(TRUE.equalsIgnoreCase(value));
    }

}
