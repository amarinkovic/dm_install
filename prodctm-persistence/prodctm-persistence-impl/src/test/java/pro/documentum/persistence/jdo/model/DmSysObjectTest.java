package pro.documentum.persistence.jdo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Query;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfSysObject;

import pro.documentum.model.jdo.sysobject.DmSysObject;
import pro.documentum.persistence.jdo.JDOTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("unchecked")
public class DmSysObjectTest extends JDOTestSupport {

    @Test
    public void testCheckOut() throws Exception {
        String objectName = RandomStringUtils.randomAscii(100);
        IDfSysObject object = (IDfSysObject) getUnderneathSession().newObject(
                "dm_document");
        object.setObjectName(objectName);
        object.save();
        String objectId = object.getObjectId().getId();
        Query query = getPersistenceManager().newQuery(DmSysObject.class,
                "objectName == :objectName && objectId == :objectId");
        Map<String, Object> params = new HashMap<>();
        params.put("objectName", objectName);
        params.put("objectId", object.getObjectId().getId());
        List<DmSysObject> results = (List<DmSysObject>) query
                .executeWithMap(params);
        assertNotNull(results);
        assertEquals(1, results.size());
        DmSysObject sysobject = results.get(0);
        assertEquals(objectId, sysobject.getObjectId());
        assertEquals(objectName, sysobject.getObjectName());
        sysobject = getPersistenceManager().detachCopy(sysobject);
        sysobject.getLockInfo().setLockOwner(
                getUnderneathSession().getLoginUserName());
        sysobject = getPersistenceManager().makePersistent(sysobject);
        getPersistenceManager().flush();
        assertEquals(objectId, sysobject.getObjectId());
        assertEquals(objectName, sysobject.getObjectName());
        object.revert();
        assertTrue(object.isCheckedOutBy(null));
        sysobject = getPersistenceManager().detachCopy(sysobject);
        objectName = RandomStringUtils.randomAscii(100);
        sysobject.getLockInfo().setLockOwner(null);
        sysobject.setObjectName(objectName);
        sysobject = getPersistenceManager().makePersistent(sysobject);
        getPersistenceManager().flush();
        assertEquals(objectId, sysobject.getObjectId());
        assertEquals(objectName, sysobject.getObjectName());
        object.revert();
        assertFalse(object.isCheckedOut());
        assertEquals(objectName, object.getObjectName());
    }

    @Test
    public void testCreateSysObject() throws Exception {
        String objectName = RandomStringUtils.randomAlphabetic(32);
        String title = RandomStringUtils.randomAlphabetic(32);
        DmSysObject object = getPersistenceManager().newInstance(
                DmSysObject.class);
        object.setObjectName(objectName);
        object = getPersistenceManager().makePersistent(object);
        getPersistenceManager().flush();
        assertEquals(0, object.getVStamp());
        assertNotNull(object.getModifyDate());
        object.setTitle(title);
        object = getPersistenceManager().makePersistent(object);
        assertEquals(title, object.getTitle());
        assertEquals(0, object.getVStamp());
        getPersistenceManager().flush();
        assertEquals(title, object.getTitle());
        assertEquals(1, object.getVStamp());
    }

}
