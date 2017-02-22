package pro.documentum.persistence.jpa.query;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class NonPersistent extends AbstractQueryTest {

    @Test
    public void testSingle() throws Exception {
        Query q = getEntityManager().createNativeQuery(
                "SELECT r_object_id, i_vstamp, i_is_replica FROM dm_user");
        List users = q.getResultList();
        assertNotNull(users);
        Object user = users.get(0);
        assertNotNull(user);
    }

}
