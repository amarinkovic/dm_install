package pro.documentum.jdo.query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.documentum.fc.common.DfTime;

import pro.documentum.model.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DateDQLMapperTest extends AbstractDQLMapperTest {

    @Test
    public void testNullDate1() throws Exception {
        String q = newQuery(DmUser.class, "modifyDate == NULLDATE");
        assertTrue(q.endsWith("WHERE this.r_modify_date IS NULLDATE"));
    }

    @Test
    public void testNullDate2() throws Exception {
        String q = newQuery(DmUser.class, "modifyDate == NULL");
        assertTrue(q.endsWith("WHERE this.r_modify_date IS NULL"));
    }

    @Test
    public void testNullDate3() throws Exception {
        String q = newQuery(DmUser.class,
                "modifyDate == NULL || modifyDate == NULLDATE");
        assertTrue(q
                .endsWith("WHERE (this.r_modify_date IS NULL) OR (this.r_modify_date IS NULLDATE)"));
    }

    @Test
    public void testDateLiteral() throws Exception {
        String q = newQuery(DmUser.class,
                "modifyDate == DATE('2016/05/15', 'yyyy/mm/dd')");
        assertTrue(q
                .endsWith("WHERE this.r_modify_date=DATE('2016/05/15','yyyy/mm/dd')"));
    }

    @Test
    public void testDateLiteralBinding() throws Exception {
        Date date = new Date();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("now", date);
        String addon = toDate(date, "yyyy/mm/dd");
        String q = newQuery(DmUser.class,
                "modifyDate == DATE(:now, 'yyyy/mm/dd')", params);
        assertTrue(q.endsWith("WHERE this.r_modify_date=" + addon));
    }

    @Test
    public void testDateLiteralSpecial1() throws Exception {
        String q = newQuery(DmUser.class, "modifyDate == DATE(now)");
        assertTrue(q.endsWith("WHERE this.r_modify_date=DATE(now)"));
    }

    @Test
    public void testDateLiteralSpecial2() throws Exception {
        String q = newQuery(DmUser.class, "modifyDate == DATE(today)");
        assertTrue(q.endsWith("WHERE this.r_modify_date=DATE(today)"));
    }

    @Test
    public void testDateLiteralSpecial3() throws Exception {
        String q = newQuery(DmUser.class, "modifyDate == DATE(yesterday)");
        assertTrue(q.endsWith("WHERE this.r_modify_date=DATE(yesterday)"));
    }

    @Test
    public void testDateLiteralSpecial4() throws Exception {
        String q = newQuery(DmUser.class, "modifyDate == DATE(tomorrow)");
        assertTrue(q.endsWith("WHERE this.r_modify_date=DATE(tomorrow)"));
    }

    @Test
    public void testDateLiteralSpecial5() throws Exception {
        String q = newQuery(DmUser.class,
                "modifyDate == DATE(dayaftertomorrow)");
        assertFalse(q
                .endsWith("WHERE this.r_modify_date=DATE(dayaftertomorrow)"));
    }

    @Test
    public void testDateBinding() throws Exception {
        Date date = new Date();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("now", date);
        String addon = toDate(date, "yyyy/mm/dd hh:mi:ss");
        String q = newQuery(DmUser.class, "modifyDate == :now", params);
        assertTrue(q.endsWith("WHERE this.r_modify_date=" + addon));
    }

    protected String toDate(Date date, String pattern) {
        DfTime time = new DfTime(date);
        return "DATE('" + time.asString(pattern) + "','" + pattern + "')";
    }

}
