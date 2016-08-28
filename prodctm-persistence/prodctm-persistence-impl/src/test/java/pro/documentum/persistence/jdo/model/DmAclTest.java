package pro.documentum.persistence.jdo.model;

import java.util.List;

import javax.jdo.Query;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import pro.documentum.model.jdo.acl.DmAcl;
import pro.documentum.persistence.jdo.JDOTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("unchecked")
public class DmAclTest extends JDOTestSupport {

    @Test
    public void testAcl() throws Exception {
        Query query = getPersistenceManager().newQuery(DmAcl.class);
        List<DmAcl> results = (List<DmAcl>) query.execute();
        assertNotNull(results);
        DmAcl acl = results.get(0);
        assertNotNull(acl.getPermits());
        query.close(results);
        assertNotNull(acl);
    }

    @Test
    public void testCreate() throws Exception {
        String aclName = RandomStringUtils.randomAlphabetic(32);
        DmAcl acl = getPersistenceManager().newInstance(DmAcl.class);
        acl.setObjectName(aclName);
        acl.setOwnerName(getLoginName());
        acl = getPersistenceManager().makePersistent(acl);
        getPersistenceManager().flush();
    }

}
