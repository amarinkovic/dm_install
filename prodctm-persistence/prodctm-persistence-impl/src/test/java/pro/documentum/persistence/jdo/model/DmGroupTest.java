package pro.documentum.persistence.jdo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDODataStoreException;
import javax.jdo.Query;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import pro.documentum.model.jdo.DmGroup;
import pro.documentum.persistence.jdo.JDOTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("unchecked")
public class DmGroupTest extends JDOTestSupport {

    @Test
    public void testQueryByName() throws Exception {
        String description = RandomStringUtils.randomAlphabetic(32);
        Query query = getPersistenceManager().newQuery(DmGroup.class,
                "(groupName == :group_name)");
        Map<String, String> params = new HashMap<>();
        params.put("group_name", "dm_superusers");
        List<DmGroup> results = (List<DmGroup>) query.executeWithMap(params);
        assertNotNull(results);
        assertEquals(1, results.size());
        DmGroup group = results.get(0);
        getPersistenceManager().flush();
        assertEquals("dm_superusers", group.getGroupName());
        int vStamp = group.getVStamp();
        group.setDescription(description);
        group = getPersistenceManager().makePersistent(group);
        assertEquals(description, group.getDescription());
        assertEquals(vStamp, group.getVStamp());
        getPersistenceManager().flush();
        assertEquals(description, group.getDescription());
        assertEquals(vStamp + 1, group.getVStamp());
    }

    @Test
    public void testDetach() throws Exception {
        String description = RandomStringUtils.randomAlphabetic(32);
        Query query = getPersistenceManager().newQuery(DmGroup.class,
                "(groupName == :group_name)");
        Map<String, String> params = new HashMap<>();
        params.put("group_name", "dm_superusers");
        List<DmGroup> results = (List<DmGroup>) query.executeWithMap(params);
        assertNotNull(results);
        assertEquals(1, results.size());
        DmGroup group = results.get(0);
        group = getPersistenceManager().detachCopy(group);
        int vStamp = group.getVStamp();
        group.setDescription(description);
        group = getPersistenceManager().makePersistent(group);
        assertEquals(description, group.getDescription());
        assertEquals(vStamp, group.getVStamp());
        getPersistenceManager().flush();
        assertEquals(description, group.getDescription());
        assertEquals(vStamp + 1, group.getVStamp());
    }

    @Test(expected = JDODataStoreException.class)
    public void testCreateExistingGroup() throws Exception {
        DmGroup group = getPersistenceManager().newInstance(DmGroup.class);
        group.setGroupName("dm_superusers");
        group = getPersistenceManager().makePersistent(group);
        getPersistenceManager().flush();
    }

    @Test
    public void testCreateGroup() throws Exception {
        String groupName = RandomStringUtils.randomAlphabetic(32);
        String description = RandomStringUtils.randomAlphabetic(32);
        DmGroup group = getPersistenceManager().newInstance(DmGroup.class);
        group.setGroupName(groupName);
        group = getPersistenceManager().makePersistent(group);
        getPersistenceManager().flush();
        assertEquals(0, group.getVStamp());
        assertNotNull(group.getModifyDate());
        group.setDescription(description);
        group = getPersistenceManager().makePersistent(group);
        assertEquals(description, group.getDescription());
        assertEquals(0, group.getVStamp());
        getPersistenceManager().flush();
        assertEquals(description, group.getDescription());
        assertEquals(1, group.getVStamp());
    }

}
