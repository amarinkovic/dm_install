package pro.documentum.persistence.common.query.expression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class DQLExpression {

    private final String _text;

    protected DQLExpression(final String text) {
        _text = text;
    }

    public String getText() {
        return _text;
    }

    public boolean isNull() {
        return false;
    }

}
