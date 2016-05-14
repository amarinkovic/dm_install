package pro.documentum.jdo.query.expression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class DQLNull extends DQLLiteral {

    DQLNull() {
        super(null);
    }

    @Override
    public boolean isNull() {
        return true;
    }

}
