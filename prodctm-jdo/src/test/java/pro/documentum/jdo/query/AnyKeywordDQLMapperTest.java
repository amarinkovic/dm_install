package pro.documentum.jdo.query;

import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

import pro.documentum.model.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AnyKeywordDQLMapperTest extends AbstractDQLMapperTest {

    @Test
    public void testAnyKeyword1() throws Exception {
        String q = newQuery(DmUser.class, "ANY (userName == 'dmadmin')");
        assertTrue(q.endsWith("WHERE ANY (this.user_name='dmadmin')"));
    }

    @Test
    public void testAnyKeyword2() throws Exception {
        String q = newQuery(DmUser.class, "ANY (userName) == 'dmadmin'");
        assertTrue(q.endsWith("WHERE ANY this.user_name='dmadmin'"));
    }

    @Test
    public void testAnyKeyword3() throws Exception {
        String q = newQuery(DmUser.class, "ANY (userName) == 'dmadmin' "
                + "|| ANY (userLoginName == 'dmadmin')");
        assertTrue(q.endsWith("WHERE (ANY this.user_name='dmadmin') "
                + "OR (ANY (this.user_login_name='dmadmin'))"));
    }

    @Test
    public void testAnyKeyword4() throws Exception {
        String q = newQuery(DmUser.class, "ANY (userName == 'dmadmin' "
                + "&& userLoginName == 'dmadmin')");
        assertTrue(q.endsWith("WHERE ANY ((this.user_name='dmadmin') "
                + "AND (this.user_login_name='dmadmin'))"));
    }

    @Test
    public void testAnyKeyword5() throws Exception {
        String q = newQuery(DmUser.class, "ANY (userName == 'dmadmin' "
                + "&& userLoginName == 'dmadmin') && ANY (modifyDate == null)");
        assertTrue(q.endsWith("WHERE (ANY ((this.user_name='dmadmin') "
                + "AND (this.user_login_name='dmadmin'))) "
                + "AND (ANY (this.r_modify_date IS NULL))"));
    }

    @Test
    public void testAnyKeyword6() throws Exception {
        String q = newQuery(DmUser.class, "ANY (userName == 'dmadmin' "
                + "&& userLoginName == 'dmadmin') && ANY (modifyDate) == null");
        assertTrue(q.endsWith("WHERE (ANY ((this.user_name='dmadmin') "
                + "AND (this.user_login_name='dmadmin'))) "
                + "AND (ANY this.r_modify_date IS NULL)"));
    }

}
