package pro.documentum.persistence.jpa.model;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import pro.documentum.model.jpa.acl.DmAcl;
import pro.documentum.persistence.jpa.query.AbstractQueryTest;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("unchecked")
public class DmAclTest extends AbstractQueryTest {

    @Test
    public void testAcl() throws Exception {
        Query query = jpa(DmAcl.class, null);
        List results = query.getResultList();
        assertNotNull(results);
        DmAcl acl = (DmAcl) results.get(0);
        close(query, results);
        assertNotNull(acl.getPermits());
        assertTrue(acl.getPermits().size() >= 2);
    }

    @Test
    public void testCreate() throws Exception {
        String aclName = RandomStringUtils.randomAlphabetic(32);
        DmAcl acl = new DmAcl();
        acl.setObjectName(aclName);
        acl.setOwnerName(getLoginName());
        getEntityManager().persist(acl);
        getEntityManager().flush();
    }

}
