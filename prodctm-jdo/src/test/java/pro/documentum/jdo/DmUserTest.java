package pro.documentum.jdo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Date;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pro.documentum.model.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DmUserTest {

    private PersistenceManager _pm;

    @Before
    public void setUp() throws Exception {
        PersistenceManagerFactory pmf = JDOHelper
                .getPersistenceManagerFactory("Testing");
        _pm = pmf.getPersistenceManager();
        Transaction tr = _pm.currentTransaction();
        tr.begin();
    }

    @After
    public void tearDown() throws Exception {
        Transaction tr = _pm.currentTransaction();
        tr.rollback();
        _pm.close();
    }

    @Test
    public void testQueryByName() throws Exception {
        String description = RandomStringUtils.randomAlphabetic(32);
        Query query = _pm.newQuery(DmUser.class,
                "(userName == :user_name || userLoginName == :user_name)");
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_name", "dmadmin");
        List<DmUser> results = (List<DmUser>) query.executeWithMap(params);
        assertNotNull(results);
        assertEquals(1, results.size());
        DmUser user = results.get(0);
        _pm.flush();
        assertEquals("dmadmin", user.getUserName());
        assertEquals(16, user.getUserPrivileges());
        int vStamp = user.getVStamp();
        user.setDescription(description);
        user = _pm.makePersistent(user);
        assertEquals(description, user.getDescription());
        assertEquals(vStamp, user.getVStamp());
        _pm.flush();
        assertEquals(description, user.getDescription());
        assertEquals(vStamp + 1, user.getVStamp());
    }

    @Test
    public void testQueryByDate() throws Exception {
        Query query = _pm.newQuery(DmUser.class, "(modifyDate <= :now)");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("now", new Date());
        List<DmUser> results = (List<DmUser>) query.executeWithMap(params);
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    public void testQueryStringEscaping() throws Exception {
        Query query = _pm.newQuery(DmUser.class, "(userName == :user_name)");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_name", "\'");
        List<DmUser> results = (List<DmUser>) query.executeWithMap(params);
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test(expected = JDODataStoreException.class)
    public void testAny() throws Exception {
        Query query = _pm.newQuery(DmUser.class,
                "ANY(userName == :user_name || userLoginName == :user_name)");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_name", "\'");
        List<DmUser> results = (List<DmUser>) query.executeWithMap(params);
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void testDetach() throws Exception {
        String description = RandomStringUtils.randomAlphabetic(32);
        Query query = _pm.newQuery(DmUser.class,
                "(userName == :user_name || userLoginName == :user_name)");
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_name", "dmadmin");
        List<DmUser> results = (List<DmUser>) query.executeWithMap(params);
        assertNotNull(results);
        assertEquals(1, results.size());
        DmUser user = results.get(0);
        user = _pm.detachCopy(user);
        int vStamp = user.getVStamp();
        user.setDescription(description);
        user = _pm.makePersistent(user);
        assertEquals(description, user.getDescription());
        assertEquals(vStamp, user.getVStamp());
        _pm.flush();
        assertEquals(description, user.getDescription());
        assertEquals(vStamp + 1, user.getVStamp());
    }

    @Test(expected = JDODataStoreException.class)
    public void testCreateExistingUser() throws Exception {
        DmUser user = _pm.newInstance(DmUser.class);
        user.setUserName("dmamdin");
        user.setUserLoginName("dmadmin");
        user = _pm.makePersistent(user);
        _pm.flush();
    }

    @Test
    public void testCreateUser() throws Exception {
        String userName = RandomStringUtils.randomAlphabetic(32);
        String description = RandomStringUtils.randomAlphabetic(32);
        DmUser user = _pm.newInstance(DmUser.class);
        user.setUserName(userName);
        user.setUserLoginName(userName);
        user = _pm.makePersistent(user);
        assertEquals(0, user.getVStamp());
        assertNotNull(user.getDefaultFolder());
        assertNotNull(user.getModifyDate());
        user.setDescription(description);
        user = _pm.makePersistent(user);
        assertEquals(description, user.getDescription());
        assertEquals(0, user.getVStamp());
        _pm.flush();
        assertEquals(description, user.getDescription());
        assertEquals(1, user.getVStamp());
    }

}
