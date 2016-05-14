package pro.documentum.jdo.query.expression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class DQLBoolean extends DQLLiteral<Boolean> {

    DQLBoolean(final Boolean value) {
        super(String.valueOf(value).toUpperCase());
    }

}
