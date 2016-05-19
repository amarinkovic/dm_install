package pro.documentum.jdo.query;

import org.junit.Test;

import pro.documentum.model.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class UpperDQLMapperTest extends AbstractDQLMapperTest {

    @Test
    public void testUpper1() throws Exception {
        String q = newQuery(DmUser.class, "UPPER(userName) == 'dmadmin'");
        assertTrue(q.endsWith("WHERE UPPER(this.user_name)='dmadmin'"));
    }

    @Test
    public void testUpper2() throws Exception {
        String q = newQuery(DmUser.class, "userName.toUpperCase() == 'dmadmin'");
        assertTrue(q.endsWith("WHERE UPPER(this.user_name)='dmadmin'"));
    }

}
