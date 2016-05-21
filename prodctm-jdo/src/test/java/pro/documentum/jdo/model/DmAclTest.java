package pro.documentum.jdo.model;

import java.util.List;

import javax.jdo.Query;

import org.junit.Test;

import pro.documentum.jdo.JDOTestSupport;
import pro.documentum.model.DmAcl;

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
        query.close(results);
        assertNotNull(acl);
    }

}
