package pro.documentum.persistence.common.query.expression.literals.nulls;

import java.util.Date;

import pro.documentum.persistence.common.query.expression.literals.DQLLiteral;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLNullDate extends DQLLiteral<Date> {

    DQLNullDate() {
        super(null, DQLNull.NULLDATE);
    }

    @Override
    public boolean isNull() {
        return true;
    }

}
