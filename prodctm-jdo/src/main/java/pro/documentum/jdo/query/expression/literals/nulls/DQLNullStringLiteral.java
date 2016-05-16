package pro.documentum.jdo.query.expression.literals.nulls;

import java.util.Date;

import pro.documentum.jdo.query.expression.literals.DQLLiteral;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLNullStringLiteral extends DQLLiteral<Date> {

    DQLNullStringLiteral() {
        super(null, "NULLSTRING");
    }

    @Override
    public boolean isNull() {
        return true;
    }

}
