package pro.documentum.jdo.query.expression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLField extends DQLExpression {

    private final boolean _repeating;

    public DQLField(final String fieldName) {
        this(fieldName, false);
    }

    public DQLField(final String fieldName, final boolean repeating) {
        super(fieldName);
        _repeating = repeating;
    }

    public boolean isRepeating() {
        return _repeating;
    }

    public static boolean isFieldExpression(final DQLExpression expression) {
        return expression instanceof DQLField;
    }

}
