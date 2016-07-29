package pro.documentum.persistence.jdo.embedded;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfPermit;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.impl.security.ExtendedPermitData;

import pro.documentum.model.jdo.DmAcl;
import pro.documentum.model.jdo.DmSysobject;
import pro.documentum.model.jdo.embedded.DmLockInfo;
import pro.documentum.model.jdo.embedded.DmPermit;
import pro.documentum.persistence.jdo.JDOTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("unchecked")
public class TestEmbedded extends JDOTestSupport {

    @Test
    public void testLockInfo() throws Exception {
        IDfSysObject object = (IDfSysObject) getSession().newObject(
                "dm_document");
        object.save();
        object.checkout();
        DmSysobject dmSysobject = getPersistenceManager().getObjectById(
                DmSysobject.class, object.getObjectId().getId());
        assertNotNull(dmSysobject.getLockInfo());
        DmLockInfo lockInfo = dmSysobject.getLockInfo();
        assertNotNull(lockInfo.getLockDate());
        assertNotNull(lockInfo.getLockMachine());
        assertNotNull(lockInfo.getLockOwner());
        assertTrue(StringUtils.isNotBlank(lockInfo.getLockOwner()));
        assertTrue(StringUtils.isNotBlank(lockInfo.getLockMachine()));

        dmSysobject = getPersistenceManager().detachCopy(dmSysobject);
        dmSysobject.getLockInfo().setLockOwner(null);
        dmSysobject = getPersistenceManager().makePersistent(dmSysobject);
        getPersistenceManager().flush();
        object.fetch(null);
        assertFalse(object.isCheckedOut());

        dmSysobject = getPersistenceManager().detachCopy(dmSysobject);
        dmSysobject.getLockInfo().setLockOwner(getSession().getLoginUserName());
        dmSysobject = getPersistenceManager().makePersistent(dmSysobject);
        getPersistenceManager().flush();
        object.fetch(null);
        assertTrue(object.isCheckedOut());

        dmSysobject.getLockInfo().setLockOwner(null);
        dmSysobject = getPersistenceManager().makePersistent(dmSysobject);
        getPersistenceManager().flush();
        object.fetch(null);
        assertFalse(object.isCheckedOut());

    }

    @Test
    public void testAcl() throws Exception {
        IDfACL object = (IDfACL) getSession().newObject("dm_acl");
        object.setObjectName(RandomStringUtils.randomAlphabetic(32));
        object.setDomain(getLoginName());
        object.save();
        DmAcl dmAcl = getPersistenceManager().getObjectById(DmAcl.class,
                object.getObjectId().getId());
        List<DmPermit> permits = dmAcl.getPermits();
        assertEquals(2, permits.size());
        assertEquals("dm_world", permits.get(0).getAccessorName());
        assertEquals("dm_owner", permits.get(1).getAccessorName());
        assertEquals(1, permits.get(0).getAccessorPermit());
        dmAcl = getPersistenceManager().detachCopy(dmAcl);
        dmAcl.getPermits().get(0).setAccessorPermit(4);
        dmAcl.getPermits().add(
                new DmPermit().setPermitType(IDfPermit.DF_ACCESS_PERMIT)
                        .setAccessorName(getLoginName()).setAccessorPermit(7)
                        .setAccessorXPermit(ExtendedPermitData.ALL));
        dmAcl.getPermits().add(
                new DmPermit().setPermitType(IDfPermit.DF_ACCESS_RESTRICTION)
                        .setAccessorName(getLoginName()).setAccessorPermit(6)
                        .setAccessorXPermit(ExtendedPermitData.ALL));
        dmAcl = getPersistenceManager().makePersistent(dmAcl);
        getPersistenceManager().flush();
        object.fetch(null);
        assertEquals(4, permits.get(0).getAccessorPermit());
        assertEquals(4, object.getAccessorCount());
        assertEquals(getLoginName(), object.getAccessorName(2));
        assertEquals(7, object.getAccessorPermit(2));
        assertEquals(ExtendedPermitData.ALL, object.getAccessorXPermit(2));
        assertEquals(IDfPermit.DF_ACCESS_PERMIT,
                object.getAccessorPermitType(2));
        assertEquals(getLoginName(), object.getAccessorName(3));
        assertEquals(6, object.getAccessorPermit(3));
        assertEquals(IDfPermit.DF_ACCESS_RESTRICTION,
                object.getAccessorPermitType(3));
    }

}
