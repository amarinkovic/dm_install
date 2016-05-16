package pro.documentum.jdo.query.expression.literals.nulls;

import java.util.HashSet;
import java.util.Set;

import org.datanucleus.query.expression.VariableExpression;

import pro.documentum.jdo.query.expression.literals.DQLLiteral;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLNullLiteral extends DQLLiteral<Void> {

    public static final Set<String> SPECIAL_NULLS;

    static {
        SPECIAL_NULLS = new HashSet<String>();
        SPECIAL_NULLS.add("NULL");
        SPECIAL_NULLS.add("NULLDATE");
        SPECIAL_NULLS.add("NULLSTRING");
    }

    public DQLNullLiteral() {
        super(null, "NULL");
    }

    @Override
    public boolean isNull() {
        return true;
    }

    public static boolean isNullExpression(final VariableExpression expression) {
        return SPECIAL_NULLS.contains(expression.getId().toUpperCase());
    }

    public static DQLLiteral getInstance(final String value) {
        if ("NULL".equalsIgnoreCase(value)) {
            return new DQLNullLiteral();
        } else if ("NULLSTRING".equalsIgnoreCase(value)) {
            return new DQLNullStringLiteral();
        } else if ("NULLDATE".equalsIgnoreCase(value)) {
            return new DQLNullDateLiteral();
        }
        return null;
    }

}
