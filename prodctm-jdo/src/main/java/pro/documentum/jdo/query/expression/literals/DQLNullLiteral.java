package pro.documentum.jdo.query.expression.literals;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLNullLiteral extends DQLLiteral<Void> {

    public DQLNullLiteral() {
        super(null, null);
    }

    @Override
    public boolean isNull() {
        return true;
    }

}
