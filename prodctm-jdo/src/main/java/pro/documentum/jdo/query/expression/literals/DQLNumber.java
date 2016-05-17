package pro.documentum.jdo.query.expression.literals;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLNumber extends DQLLiteral<Number> {

    public DQLNumber(final Number value) {
        super(value, String.valueOf(value));
    }

}
