package pro.documentum.jdo.query;

import org.junit.Test;

import pro.documentum.model.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class LowerDQLMapperTest extends AbstractDQLMapperTest {

    @Test
    public void testUpper1() throws Exception {
        String q = newQuery(DmUser.class, "LOWER(userName) == 'dmadmin'");
        assertTrue(q.endsWith("WHERE LOWER(this.user_name)='dmadmin'"));
    }

    @Test
    public void testUpper2() throws Exception {
        String q = newQuery(DmUser.class, "userName.toLowerCase() == 'dmadmin'");
        assertTrue(q.endsWith("WHERE LOWER(this.user_name)='dmadmin'"));
    }

}
