package pro.documentum.jdo.query.expression.literals.nulls;

import java.util.HashSet;
import java.util.Set;

import org.datanucleus.query.expression.VariableExpression;

import pro.documentum.jdo.query.IDQLEvaluator;
import pro.documentum.jdo.query.IVariableEvaluator;
import pro.documentum.jdo.query.expression.DQLExpression;
import pro.documentum.jdo.query.expression.literals.DQLLiteral;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLNull extends DQLLiteral<Void> {

    public static final String NULL = "NULL";

    public static final String NULLDATE = "NULLDATE";

    public static final String NULLSTRING = "NULLSTRING";

    public static final Set<String> SPECIAL_NULLS;

    static {
        SPECIAL_NULLS = new HashSet<String>();
        SPECIAL_NULLS.add(NULL);
        SPECIAL_NULLS.add(NULLDATE);
        SPECIAL_NULLS.add(NULLSTRING);
    }

    public DQLNull() {
        super(null, NULL);
    }

    @Override
    public boolean isNull() {
        return true;
    }

    private static DQLExpression evaluate(final VariableExpression expression,
            final IDQLEvaluator evaluator) {
        String name = expression.getId();
        if (!SPECIAL_NULLS.contains(name.toUpperCase())) {
            return null;
        }
        return getInstance(name);
    }

    private static DQLLiteral getInstance(final String value) {
        if (NULL.equalsIgnoreCase(value)) {
            return new DQLNull();
        } else if (NULLSTRING.equalsIgnoreCase(value)) {
            return new DQLNullString();
        } else if (NULLDATE.equalsIgnoreCase(value)) {
            return new DQLNullDate();
        }
        return null;
    }

    public static IVariableEvaluator getVariableEvaluator() {
        return new IVariableEvaluator() {
            @Override
            public DQLExpression evaluate(final VariableExpression expression,
                    final IDQLEvaluator evaluator) {
                return DQLNull.evaluate(expression, evaluator);
            }
        };
    }

}
