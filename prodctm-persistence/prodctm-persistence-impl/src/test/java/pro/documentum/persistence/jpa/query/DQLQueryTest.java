package pro.documentum.persistence.jpa.query;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import pro.documentum.model.jpa.user.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings({"unchecked", "rawtypes" })
public class DQLQueryTest extends AbstractQueryTest {

    @Test
    public void testDQL1() throws Exception {
        Query q = getEntityManager().createNativeQuery(
                "SELECT r_object_id, i_vstamp, i_is_replica FROM dm_user",
                DmUser.class);
        List<DmUser> users = (List<DmUser>) q.getResultList();
        assertNotNull(users);
        DmUser user = users.get(0);
        assertNotNull(user);
        assertNotNull(user.getUserLoginName());
    }

}
