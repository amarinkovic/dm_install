package pro.documentum.jdo.query.expression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class DQLExpression {

    private String _dqlText;

    public String getDqlText() {
        return _dqlText;
    }

    protected void setDqlText(final String dqlText) {
        _dqlText = dqlText;
    }

}
