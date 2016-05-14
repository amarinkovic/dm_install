package pro.documentum.jdo.query.expression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLFieldExpression extends DQLExpression {

    private final boolean _repeating;

    public DQLFieldExpression(final String fieldName) {
        this(fieldName, false);
    }

    public DQLFieldExpression(final String fieldName, final boolean repeating) {
        super(fieldName);
        _repeating = repeating;
    }

    public boolean isRepeating() {
        return _repeating;
    }

}
