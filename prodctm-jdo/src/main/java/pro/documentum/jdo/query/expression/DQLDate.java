package pro.documentum.jdo.query.expression;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class DQLDate extends DQLLiteral<Date> {

    DQLDate(final Date value) {
        super(toString(value));
    }

    private static String toString(final Date date) {
        return "DATE('"
                + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date)
                + "', 'yyyy/mm/dd hh:mi:ss')";
    }

}
