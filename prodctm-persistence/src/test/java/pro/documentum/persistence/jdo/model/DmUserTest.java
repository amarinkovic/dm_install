package pro.documentum.persistence.jdo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDODataStoreException;
import javax.jdo.Query;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import pro.documentum.model.jdo.DmUser;
import pro.documentum.persistence.jdo.JDOTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("unchecked")
public class DmUserTest extends JDOTestSupport {

    @Test
    public void testQueryByName2() throws Exception {
        String userName = getUnderneathSession().getLoginUserName();
        String description = RandomStringUtils.randomAlphabetic(32);
        Query query = getPersistenceManager().newQuery(DmUser.class,
                "(userName == :user_name || userLoginName == :user_name)");
        Map<String, String> params = new HashMap<>();
        params.put("user_name", userName);
        List<DmUser> results = (List<DmUser>) query.executeWithMap(params);
        assertNotNull(results);
        assertEquals(1, results.size());
        DmUser user = results.get(0);
        getPersistenceManager().flush();
        assertEquals(userName, user.getUserName());
        assertEquals(16, user.getUserPrivileges());
        int vStamp = user.getVStamp();
        user.setDescription(description);
        user = getPersistenceManager().makePersistent(user);
        assertEquals(description, user.getDescription());
        assertEquals(vStamp, user.getVStamp());
        getPersistenceManager().flush();
        assertEquals(description, user.getDescription());
        assertEquals(vStamp + 1, user.getVStamp());
    }

    @Test
    public void testDetach() throws Exception {
        String userName = getUnderneathSession().getLoginUserName();
        String description = RandomStringUtils.randomAlphabetic(32);
        Query query = getPersistenceManager().newQuery(DmUser.class,
                "(userName == :user_name || userLoginName == :user_name)");
        Map<String, String> params = new HashMap<>();
        params.put("user_name", userName);
        List<DmUser> results = (List<DmUser>) query.executeWithMap(params);
        assertNotNull(results);
        assertEquals(1, results.size());
        DmUser user = results.get(0);
        user = getPersistenceManager().detachCopy(user);
        int vStamp = user.getVStamp();
        user.setDescription(description);
        user = getPersistenceManager().makePersistent(user);
        assertEquals(description, user.getDescription());
        assertEquals(vStamp, user.getVStamp());
        getPersistenceManager().flush();
        assertEquals(description, user.getDescription());
        assertEquals(vStamp + 1, user.getVStamp());
    }

    @Test(expected = JDODataStoreException.class)
    public void testCreateExistingUser() throws Exception {
        String userName = getUnderneathSession().getLoginUserName();
        DmUser user = getPersistenceManager().newInstance(DmUser.class);
        user.setUserName(userName);
        user.setUserLoginName(userName);
        user = getPersistenceManager().makePersistent(user);
        getPersistenceManager().flush();
    }

    @Test
    public void testCreateUser() throws Exception {
        String userName = RandomStringUtils.randomAlphabetic(32);
        String description = RandomStringUtils.randomAlphabetic(32);
        DmUser user = getPersistenceManager().newInstance(DmUser.class);
        user.setUserName(userName);
        user.setUserLoginName(userName);
        user = getPersistenceManager().makePersistent(user);
        getPersistenceManager().flush();
        assertEquals(0, user.getVStamp());
        assertNotNull(user.getDefaultFolder());
        assertNotNull(user.getModifyDate());
        user.setDescription(description);
        user = getPersistenceManager().makePersistent(user);
        assertEquals(description, user.getDescription());
        assertEquals(0, user.getVStamp());
        getPersistenceManager().flush();
        assertEquals(description, user.getDescription());
        assertEquals(1, user.getVStamp());
    }

}
