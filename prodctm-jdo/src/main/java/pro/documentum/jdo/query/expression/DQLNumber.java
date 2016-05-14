package pro.documentum.jdo.query.expression;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class DQLNumber extends DQLLiteral<Number> {

    DQLNumber(final Number value) {
        super(String.valueOf(value));
    }

}
