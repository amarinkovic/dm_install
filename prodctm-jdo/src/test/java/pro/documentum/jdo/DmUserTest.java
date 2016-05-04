package pro.documentum.jdo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDODataStoreException;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import pro.documentum.model.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DmUserTest {

    @Test
    public void testQueryByName() throws Exception {
        PersistenceManagerFactory pmf = JDOHelper
                .getPersistenceManagerFactory("Testing");
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tr = pm.currentTransaction();
        tr.begin();
        String description = RandomStringUtils.randomAlphabetic(32);
        Query query = pm.newQuery(DmUser.class,
                "(userName == :user_name || userLoginName == :user_name)");
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_name", "dmadmin");
        List<DmUser> results = (List<DmUser>) query.executeWithMap(params);
        assertNotNull(results);
        assertEquals(1, results.size());
        DmUser user = results.get(0);
        pm.flush();
        assertEquals("dmadmin", user.getUserName());
        assertEquals(16, user.getUserPrivileges());
        int vStamp = user.getVStamp();
        user.setDescription(description);
        user = pm.makePersistent(user);
        assertEquals(description, user.getDescription());
        assertEquals(vStamp, user.getVStamp());
        pm.flush();
        assertEquals(description, user.getDescription());
        assertEquals(vStamp + 1, user.getVStamp());
        tr.rollback();
        pm.close();
    }

    @Test(expected = JDODataStoreException.class)
    public void testCreateExistingUser() throws Exception {
        PersistenceManagerFactory pmf = JDOHelper
                .getPersistenceManagerFactory("Testing");
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tr = pm.currentTransaction();
        tr.begin();
        DmUser user = pm.newInstance(DmUser.class);
        user.setUserName("dmamdin");
        user.setUserLoginName("dmadmin");
        user = pm.makePersistent(user);
        pm.flush();
        tr.rollback();
        pm.close();
    }

    @Test
    public void testCreateUser() throws Exception {
        PersistenceManagerFactory pmf = JDOHelper
                .getPersistenceManagerFactory("Testing");
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tr = pm.currentTransaction();
        tr.begin();
        String userName = RandomStringUtils.randomAlphabetic(32);
        String description = RandomStringUtils.randomAlphabetic(32);
        DmUser user = pm.newInstance(DmUser.class);
        user.setUserName(userName);
        user.setUserLoginName(userName);
        user = pm.makePersistent(user);
        assertEquals(0, user.getVStamp());
        assertNotNull(user.getDefaultFolder());
        assertNotNull(user.getModifyDate());
        user.setDescription(description);
        user = pm.makePersistent(user);
        assertEquals(description, user.getDescription());
        assertEquals(0, user.getVStamp());
        pm.flush();
        assertEquals(description, user.getDescription());
        assertEquals(1, user.getVStamp());
        tr.rollback();
        pm.close();
    }

}
