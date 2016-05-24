package pro.documentum.persistence.jdo.query;

import java.util.List;

import org.junit.Test;

import pro.documentum.model.jdo.DmUser;
import pro.documentum.persistence.common.query.DQLQuery;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings({"unchecked", "rawtypes" })
public class DQLQueryTest extends AbstractQueryTest {

    @Test
    public void testDQL1() throws Exception {
        DQLQuery q = dql(DmUser.class,
                "SELECT r_object_id, i_vstamp FROM dm_user");
        List<DmUser> users = (List<DmUser>) q.execute();
        assertNotNull(users);
        DmUser user = users.get(0);
        q.close(users);
        assertNotNull(user);
        assertNotNull(user.getUserLoginName());
    }

}
