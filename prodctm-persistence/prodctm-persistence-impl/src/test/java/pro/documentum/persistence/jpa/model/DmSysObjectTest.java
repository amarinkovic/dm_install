package pro.documentum.persistence.jpa.model;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfSysObject;

import pro.documentum.model.jpa.sysobject.DmSysObject;
import pro.documentum.persistence.jpa.query.AbstractQueryTest;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("unchecked")
public class DmSysObjectTest extends AbstractQueryTest {

    @Test
    public void testCheckOut() throws Exception {
        String objectName = RandomStringUtils.randomAscii(100);
        IDfSysObject object = (IDfSysObject) getUnderneathSession().newObject(
                "dm_document");
        object.setObjectName(objectName);
        object.save();
        String objectId = object.getObjectId().getId();
        Query q = jpa(DmSysObject.class, "objectName = :objectName "
                + "and objectId = :objectId");
        q.setParameter("objectName", objectName);
        q.setParameter("objectId", objectId);
        List results = q.getResultList();

        assertNotNull(results);
        assertEquals(1, results.size());
        DmSysObject sysobject = (DmSysObject) results.get(0);
        assertEquals(objectId, sysobject.getObjectId());
        assertEquals(objectName, sysobject.getObjectName());
        assertNotNull(sysobject.getAcl());
        getEntityManager().detach(sysobject);
        sysobject.getLockInfo().setLockOwner(
                getUnderneathSession().getLoginUserName());
        sysobject = getEntityManager().merge(sysobject);
        getEntityManager().flush();
        assertEquals(objectId, sysobject.getObjectId());
        assertEquals(objectName, sysobject.getObjectName());
        object.revert();
        assertTrue(object.isCheckedOutBy(null));
        getEntityManager().detach(sysobject);
        objectName = RandomStringUtils.randomAscii(100);
        sysobject.getLockInfo().setLockOwner(null);
        sysobject.setObjectName(objectName);
        sysobject = getEntityManager().merge(sysobject);
        getEntityManager().flush();
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
        DmSysObject object = new DmSysObject().setObjectName(objectName);
        getEntityManager().persist(object);
        getEntityManager().flush();
        assertEquals(0, object.getVStamp());
        assertNotNull(object.getModifyDate());
        object.setTitle(title);
        getEntityManager().persist(object);
        assertEquals(title, object.getTitle());
        assertEquals(0, object.getVStamp());
        getEntityManager().flush();
        assertEquals(title, object.getTitle());
        assertEquals(1, object.getVStamp());
    }

}
