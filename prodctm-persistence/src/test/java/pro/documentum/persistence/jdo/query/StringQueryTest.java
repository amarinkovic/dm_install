package pro.documentum.persistence.jdo.query;

import static org.hamcrest.Matchers.endsWith;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import pro.documentum.model.jdo.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class StringQueryTest extends AbstractQueryTest {

    @Test
    public void testQuoted1() throws Exception {
        String q = str(jdo(DmUser.class, "userName == \"aa'aaa\""));
        assertThat(q, endsWith("WHERE this.user_name='aa''aaa'"));
    }

    @Test
    public void testNullString1() throws Exception {
        String q = str(jdo(DmUser.class, "userName == NULLSTRING"));
        assertThat(q, endsWith("WHERE this.user_name IS NULLSTRING"));
    }

    @Test
    public void testNullString2() throws Exception {
        String q = str(jdo(DmUser.class, "userName == NULL"));
        assertThat(q, endsWith("WHERE this.user_name IS NULL"));
    }

    @Test
    public void testNullString3() throws Exception {
        String q = str(jdo(DmUser.class, "userName == NULL "
                + "|| userName == NULLSTRING"));
        assertThat(q, endsWith("WHERE (this.user_name IS NULL) "
                + "OR (this.user_name IS NULLSTRING)"));
    }

    @Test
    public void testUserKeyword() throws Exception {
        String q = str(jdo(DmUser.class, "userName == USER"));
        assertThat(q, endsWith("WHERE this.user_name=USER"));
    }

    @Test
    public void testStringLiteralEq() throws Exception {
        String q = str(jdo(DmUser.class, "userName == 'dmadmin'"));
        assertThat(q, endsWith("WHERE this.user_name='dmadmin'"));
    }

    @Test
    public void testStringLiteralNotEq() throws Exception {
        String q = str(jdo(DmUser.class, "userName != 'dmadmin'"));
        assertThat(q, endsWith("WHERE this.user_name!='dmadmin'"));
    }

    @Test
    public void testStringLiteralGt1() throws Exception {
        String q = str(jdo(DmUser.class, "userName > 'dmadmin'"));
        assertThat(q, endsWith("WHERE this.user_name>'dmadmin'"));
    }

    @Test
    public void testStringLiteralGt2() throws Exception {
        String q = str(jdo(DmUser.class, "'dmadmin'>userName"));
        assertThat(q, endsWith("WHERE 'dmadmin'>this.user_name"));
    }

    @Test
    public void testStringLiteralGtEq1() throws Exception {
        String q = str(jdo(DmUser.class, "userName >= 'dmadmin'"));
        assertThat(q, endsWith("WHERE this.user_name>='dmadmin'"));
    }

    @Test
    public void testStringLiteralGtEq2() throws Exception {
        String q = str(jdo(DmUser.class, "'dmadmin'>=userName"));
        assertThat(q, endsWith("WHERE 'dmadmin'>=this.user_name"));
    }

    @Test
    public void testStringLiteralLt1() throws Exception {
        String q = str(jdo(DmUser.class, "userName < 'dmadmin'"));
        assertThat(q, endsWith("WHERE this.user_name<'dmadmin'"));
    }

    @Test
    public void testStringLiteralLt2() throws Exception {
        String q = str(jdo(DmUser.class, "'dmadmin'<userName"));
        assertThat(q, endsWith("WHERE 'dmadmin'<this.user_name"));
    }

    @Test
    public void testStringLiteralLtEq1() throws Exception {
        String q = str(jdo(DmUser.class, "userName <= 'dmadmin'"));
        assertThat(q, endsWith("WHERE this.user_name<='dmadmin'"));
    }

    @Test
    public void testStringLiteralLtEq2() throws Exception {
        String q = str(jdo(DmUser.class, "'dmadmin'<=userName"));
        assertThat(q, endsWith("WHERE 'dmadmin'<=this.user_name"));
    }

    @Test
    public void testStringBindingEq() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("user_name", "dmadmin");
        String q = str(jdo(DmUser.class, "userName == :user_name"), params);
        assertThat(q, endsWith("WHERE this.user_name='dmadmin'"));
    }

    @Test
    public void testStringNullBinding1() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("user_name", null);
        String q = str(jdo(DmUser.class, "userName == :user_name"), params);
        assertThat(q, endsWith("WHERE this.user_name IS NULL"));
    }

    @Test
    public void testStringNull() throws Exception {
        Map<String, Object> params = new HashMap<>();
        String q = str(jdo(DmUser.class, "userName == null"), params);
        assertThat(q, endsWith("WHERE this.user_name IS NULL"));
    }

}
