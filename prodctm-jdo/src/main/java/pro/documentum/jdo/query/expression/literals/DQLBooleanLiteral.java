package pro.documentum.jdo.query.expression.literals;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLBooleanLiteral extends DQLLiteral<Boolean> {

    DQLBooleanLiteral(final Boolean value) {
        super(value, String.valueOf(value).toUpperCase());
    }

}
