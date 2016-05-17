package pro.documentum.jdo.query;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import pro.documentum.model.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DateToStringDQLMapperTest extends AbstractDQLMapperTest {

    @Test
    public void testDateToString() throws Exception {
        String q = newQuery(DmUser.class, "DATETOSTRING(modifyDate,"
                + "'yyyy/mm/dd hh:mi:ss') == '2016/05/18 00:00:00'");
        assertTrue(q.endsWith("WHERE DATETOSTRING(this.r_modify_date,"
                + "'yyyy/mm/dd hh:mi:ss')='2016/05/18 00:00:00'"));
    }

    @Test
    public void testDateToStringFormatBinding() throws Exception {
        String format = "yyyy/mm/dd hh:mi:ss";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("format", format);
        String q = newQuery(DmUser.class, "DATETOSTRING(modifyDate,"
                + ":format) == '2016/05/18 00:00:00'", params);
        assertTrue(q.endsWith("WHERE DATETOSTRING(this.r_modify_date," + "'"
                + format + "')='2016/05/18 00:00:00'"));
    }

}
