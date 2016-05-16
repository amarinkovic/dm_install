package pro.documentum.jdo.query;

import static junit.framework.TestCase.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import pro.documentum.model.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class StringDQLMapperTest extends AbstractDQLMapperTest {

    @Test
    public void testQuoted1() throws Exception {
        String q = newQuery(DmUser.class, "userName == \"aa'aaa\"");
        assertTrue(q.endsWith("WHERE this.user_name='aa''aaa'"));
    }

    @Test
    public void testNullString1() throws Exception {
        String q = newQuery(DmUser.class, "userName == NULLSTRING");
        assertTrue(q.endsWith("WHERE this.user_name IS NULLSTRING"));
    }

    @Test
    public void testNullString2() throws Exception {
        String q = newQuery(DmUser.class, "userName == NULL");
        assertTrue(q.endsWith("WHERE this.user_name IS NULL"));
    }

    @Test
    public void testNullString3() throws Exception {
        String q = newQuery(DmUser.class,
                "userName == NULL || userName == NULLSTRING");
        assertTrue(q
                .endsWith("WHERE (this.user_name IS NULL) OR (this.user_name IS NULLSTRING)"));
    }

    @Test
    public void testUserKeyword() throws Exception {
        String q = newQuery(DmUser.class, "userName == USER");
        assertTrue(q.endsWith("WHERE this.user_name=USER"));
    }

    @Test
    public void testStringLiteralEq() throws Exception {
        String q = newQuery(DmUser.class, "userName == 'dmadmin'");
        assertTrue(q.endsWith("WHERE this.user_name='dmadmin'"));
    }

    @Test
    public void testStringLiteralNotEq() throws Exception {
        String q = newQuery(DmUser.class, "userName != 'dmadmin'");
        assertTrue(q.endsWith("WHERE this.user_name!='dmadmin'"));
    }

    @Test
    public void testStringLiteralGt1() throws Exception {
        String q = newQuery(DmUser.class, "userName > 'dmadmin'");
        assertTrue(q.endsWith("WHERE this.user_name>'dmadmin'"));
    }

    @Test
    public void testStringLiteralGt2() throws Exception {
        String q = newQuery(DmUser.class, "'dmadmin'>userName");
        assertTrue(q.endsWith("WHERE 'dmadmin'>this.user_name"));
    }

    @Test
    public void testStringLiteralGtEq1() throws Exception {
        String q = newQuery(DmUser.class, "userName >= 'dmadmin'");
        assertTrue(q.endsWith("WHERE this.user_name>='dmadmin'"));
    }

    @Test
    public void testStringLiteralGtEq2() throws Exception {
        String q = newQuery(DmUser.class, "'dmadmin'>=userName");
        assertTrue(q.endsWith("WHERE 'dmadmin'>=this.user_name"));
    }

    @Test
    public void testStringLiteralLt1() throws Exception {
        String q = newQuery(DmUser.class, "userName < 'dmadmin'");
        assertTrue(q.endsWith("WHERE this.user_name<'dmadmin'"));
    }

    @Test
    public void testStringLiteralLt2() throws Exception {
        String q = newQuery(DmUser.class, "'dmadmin'<userName");
        assertTrue(q.endsWith("WHERE 'dmadmin'<this.user_name"));
    }

    @Test
    public void testStringLiteralLtEq1() throws Exception {
        String q = newQuery(DmUser.class, "userName <= 'dmadmin'");
        assertTrue(q.endsWith("WHERE this.user_name<='dmadmin'"));
    }

    @Test
    public void testStringLiteralLtEq2() throws Exception {
        String q = newQuery(DmUser.class, "'dmadmin'<=userName");
        assertTrue(q.endsWith("WHERE 'dmadmin'<=this.user_name"));
    }

    @Test
    public void testStringBindingEq() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_name", "dmadmin");
        String q = newQuery(DmUser.class, "userName == :user_name", params);
        assertTrue(q.endsWith("WHERE this.user_name='dmadmin'"));
    }

    @Test
    public void testStringNullBinding1() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_name", null);
        String q = newQuery(DmUser.class, "userName == :user_name", params);
        assertTrue(q.endsWith("WHERE this.user_name IS NULL"));
    }

    @Test
    public void testStringNull() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        String q = newQuery(DmUser.class, "userName == null", params);
        assertTrue(q.endsWith("WHERE this.user_name IS NULL"));
    }

}
