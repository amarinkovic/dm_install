package pro.documentum.jdo.query.expression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLFieldExpression extends DQLExpression {

    private String _fieldName;

    public DQLFieldExpression(final String fieldName) {
        _fieldName = fieldName;
        setDqlText(fieldName);
    }

    public String getFieldName() {
        return _fieldName;
    }

}
