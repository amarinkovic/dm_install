package pro.documentum.jdo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Query;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfSysObject;

import pro.documentum.model.DmSysobject;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DmSysobjectTest extends JDOTestSupport {

    @Test
    public void testLock() throws Exception {
        String objectName = RandomStringUtils.randomAscii(100);
        IDfSysObject object = (IDfSysObject) getSession().newObject(
                "dm_document");
        object.setObjectName(objectName);
        object.save();
        Query query = getPersistenceManager().newQuery(DmSysobject.class,
                "objectName == :objectName && objectId == :objectId");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("objectName", objectName);
        params.put("objectId", object.getObjectId().getId());
        List<DmSysobject> results = (List<DmSysobject>) query
                .executeWithMap(params);
        assertNotNull(results);
        assertEquals(1, results.size());
        DmSysobject sysobject = results.get(0);
        assertEquals(sysobject.getObjectId(), object.getObjectId().getId());
        assertEquals(sysobject.getObjectName(), object.getObjectName());
        sysobject = getPersistenceManager().detachCopy(sysobject);
        sysobject.setLockOwner(getSession().getLoginUserName());
        sysobject = getPersistenceManager().makePersistent(sysobject);
        getPersistenceManager().flush();
        assertEquals(sysobject.getObjectId(), object.getObjectId().getId());
        assertEquals(sysobject.getObjectName(), object.getObjectName());
        assertTrue(object.isCheckedOutBy(null));
    }

}
