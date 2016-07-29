package pro.documentum.persistence.jdo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Query;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfSysObject;

import pro.documentum.model.jdo.DmSysobject;
import pro.documentum.persistence.jdo.JDOTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("unchecked")
public class DmSysobjectTest extends JDOTestSupport {

    @Test
    public void testCheckOut() throws Exception {
        String objectName = RandomStringUtils.randomAscii(100);
        IDfSysObject object = (IDfSysObject) getUnderneathSession().newObject(
                "dm_document");
        object.setObjectName(objectName);
        object.save();
        String objectId = object.getObjectId().getId();
        Query query = getPersistenceManager().newQuery(DmSysobject.class,
                "objectName == :objectName && objectId == :objectId");
        Map<String, Object> params = new HashMap<>();
        params.put("objectName", objectName);
        params.put("objectId", object.getObjectId().getId());
        List<DmSysobject> results = (List<DmSysobject>) query
                .executeWithMap(params);
        assertNotNull(results);
        assertEquals(1, results.size());
        DmSysobject sysobject = results.get(0);
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
}
