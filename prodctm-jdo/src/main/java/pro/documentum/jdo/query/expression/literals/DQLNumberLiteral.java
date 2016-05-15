package pro.documentum.jdo.query.expression.literals;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLNumberLiteral extends DQLLiteral<Number> {

    public DQLNumberLiteral(final Number value) {
        super(value, String.valueOf(value));
    }

}
