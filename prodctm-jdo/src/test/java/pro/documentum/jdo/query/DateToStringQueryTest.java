package pro.documentum.jdo.query;

import static org.hamcrest.Matchers.endsWith;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import pro.documentum.model.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DateToStringQueryTest extends AbstractQueryTest {

    @Test
    public void testDateToString() throws Exception {
        String q = str(jdo(DmUser.class, "DATETOSTRING(modifyDate,"
                + "'yyyy/mm/dd hh:mi:ss') == '2016/05/18 00:00:00'"));
        assertThat(q, endsWith("WHERE DATETOSTRING(this.r_modify_date,"
                + "'yyyy/mm/dd hh:mi:ss')='2016/05/18 00:00:00'"));
    }

    @Test
    public void testDateToStringFormatBinding() throws Exception {
        String format = "yyyy/mm/dd hh:mi:ss";
        Map<String, Object> params = new HashMap<>();
        params.put("format", format);
        String q = str(jdo(DmUser.class, "DATETOSTRING(modifyDate,"
                + ":format) == '2016/05/18 00:00:00'"), params);
        assertThat(q, endsWith("WHERE DATETOSTRING(this.r_modify_date," + "'"
                + format + "')='2016/05/18 00:00:00'"));
    }
}
