package pro.documentum.persistence.jpa.query;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.documentum.fc.common.DfTime;

import pro.documentum.model.jpa.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DateQueryTest extends AbstractQueryTest {

    @Test
    @Ignore
    public void testNullDate1() throws Exception {
        String q = str(jpa(DmUser.class, "modifyDate = NULLDATE"));
        assertThat(q, endsWith("WHERE this.r_modify_date IS NULLDATE"));
    }

    @Test
    public void testNullDate2() throws Exception {
        String q = str(jpa(DmUser.class, "modifyDate = NULL"));
        assertThat(q, endsWith("WHERE this.r_modify_date IS NULL"));
    }

    @Test
    @Ignore
    public void testNullDate3() throws Exception {
        String q = str(jpa(DmUser.class, "modifyDate = NULL "
                + "OR modifyDate == NULLDATE"));
        assertThat(q, endsWith("WHERE (this.r_modify_date IS NULL) "
                + "OR (this.r_modify_date IS NULLDATE)"));
    }

    @Test
    public void testDateLiteral() throws Exception {
        String q = str(jpa(DmUser.class, "modifyDate = "
                + "DATE('2016/05/15', 'yyyy/mm/dd')"));
        assertThat(q, endsWith("WHERE this.r_modify_date"
                + "=DATE('2016/05/15','yyyy/mm/dd')"));
    }

    @Test
    public void testDateLiteralBinding() throws Exception {
        Date date = new Date();
        Map<String, Object> params = new HashMap<>();
        params.put("now", date);
        String addon = toDate(date, "yyyy/mm/dd");
        String q = str(jpa(DmUser.class, "modifyDate = "
                + "DATE(:now, 'yyyy/mm/dd')"), params);
        assertThat(q, endsWith("WHERE this.r_modify_date=" + addon));
    }

    @Test
    @Ignore
    public void testDateLiteralSpecial1() throws Exception {
        String q = str(jpa(DmUser.class, "modifyDate = DATE(now)"));
        assertThat(q, endsWith("WHERE this.r_modify_date=DATE(NOW)"));
    }

    @Test
    @Ignore
    public void testDateLiteralSpecial2() throws Exception {
        String q = str(jpa(DmUser.class, "modifyDate = DATE(today)"));
        assertThat(q, endsWith("WHERE this.r_modify_date=DATE(TODAY)"));
    }

    @Test
    @Ignore
    public void testDateLiteralSpecial3() throws Exception {
        String q = str(jpa(DmUser.class, "modifyDate = DATE(yesterday)"));
        assertThat(q, endsWith("WHERE this.r_modify_date=DATE(YESTERDAY)"));
    }

    @Test
    @Ignore
    public void testDateLiteralSpecial4() throws Exception {
        String q = str(jpa(DmUser.class, "modifyDate = DATE(tomorrow)"));
        assertThat(q, endsWith("WHERE this.r_modify_date=DATE(TOMORROW)"));
    }

    @Test
    @Ignore
    public void testDateLiteralSpecial5() throws Exception {
        String q = str(jpa(DmUser.class, "modifyDate = "
                + "DATE(dayaftertomorrow)"));
        assertThat(q, not(endsWith("WHERE this.r_modify_date"
                + "=DATE(dayaftertomorrow)")));
    }

    @Test
    public void testDateBinding() throws Exception {
        Date date = new Date();
        Map<String, Object> params = new HashMap<>();
        params.put("now", date);
        String addon = toDate(date, "yyyy/mm/dd hh:mi:ss");
        String q = str(jpa(DmUser.class, "modifyDate = :now"), params);
        assertThat(q, endsWith("WHERE this.r_modify_date=" + addon));
    }

    protected String toDate(Date date, String pattern) {
        DfTime time = new DfTime(date);
        return "DATE('" + time.asString(pattern) + "','" + pattern + "')";
    }

}
