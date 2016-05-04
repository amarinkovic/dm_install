package pro.documentum.jdo.query.expression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLLiteral extends DQLExpression {

    private Object _value;

    public DQLLiteral(final Object value) {
        _value = value;
        setDqlText(toString());
    }

    public Object getValue() {
        return _value;
    }

    public String toString() {
        return String.valueOf(_value);
    }

}
