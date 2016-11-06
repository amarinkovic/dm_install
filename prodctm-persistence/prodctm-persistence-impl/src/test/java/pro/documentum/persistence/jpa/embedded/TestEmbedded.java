package pro.documentum.persistence.jpa.embedded;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfPermit;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.impl.security.ExtendedPermitData;

import pro.documentum.model.jpa.acl.DmAcl;
import pro.documentum.model.jpa.embedded.acl.DmPermit;
import pro.documentum.model.jpa.embedded.sysobject.DmLockInfo;
import pro.documentum.model.jpa.sysobject.DmSysObject;
import pro.documentum.persistence.jpa.JPATestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings("unchecked")
public class TestEmbedded extends JPATestSupport {

    @Test
    public void testLockInfo() throws Exception {
        IDfSysObject object = (IDfSysObject) getUnderneathSession().newObject(
                "dm_document");
        object.save();
        object.checkout();
        DmSysObject dmSysObject = getEntityManager().find(DmSysObject.class,
                object.getObjectId().getId());
        assertNotNull(dmSysObject.getLockInfo());
        DmLockInfo lockInfo = dmSysObject.getLockInfo();
        assertNotNull(lockInfo.getLockDate());
        assertNotNull(lockInfo.getLockMachine());
        assertNotNull(lockInfo.getLockOwner());
        assertTrue(StringUtils.isNotBlank(lockInfo.getLockOwner()));
        assertTrue(StringUtils.isNotBlank(lockInfo.getLockMachine()));

        getEntityManager().detach(dmSysObject);
        dmSysObject.getLockInfo().setLockOwner(null);
        dmSysObject = getEntityManager().merge(dmSysObject);
        getEntityManager().flush();
        object.fetch(null);
        assertFalse(object.isCheckedOut());

        getEntityManager().detach(dmSysObject);
        dmSysObject.getLockInfo().setLockOwner(
                getUnderneathSession().getLoginUserName());
        dmSysObject = getEntityManager().merge(dmSysObject);
        getEntityManager().flush();
        object.fetch(null);
        assertTrue(object.isCheckedOut());

        dmSysObject.getLockInfo().setLockOwner(null);
        dmSysObject = getEntityManager().merge(dmSysObject);
        getEntityManager().flush();
        object.fetch(null);
        assertFalse(object.isCheckedOut());

    }

    @Test
    public void testAcl() throws Exception {
        IDfACL object = (IDfACL) getUnderneathSession().newObject("dm_acl");
        object.setObjectName(RandomStringUtils.randomAlphabetic(32));
        object.setDomain(getLoginName());
        object.save();
        DmAcl dmAcl = getEntityManager().find(DmAcl.class,
                object.getObjectId().getId());
        List<DmPermit> permits = dmAcl.getPermits();
        assertEquals(2, permits.size());
        assertEquals("dm_world", permits.get(0).getAccessorName());
        assertEquals("dm_owner", permits.get(1).getAccessorName());
        assertEquals(1, permits.get(0).getAccessorPermit());
        getEntityManager().detach(dmAcl);
        dmAcl.getPermits().get(0).setAccessorPermit(4);
        dmAcl.getPermits().add(
                new DmPermit().setPermitType(IDfPermit.DF_ACCESS_PERMIT)
                        .setAccessorName(getLoginName()).setAccessorPermit(7)
                        .setAccessorXPermit(ExtendedPermitData.ALL));
        dmAcl.getPermits().add(
                new DmPermit().setPermitType(IDfPermit.DF_ACCESS_RESTRICTION)
                        .setAccessorName(getLoginName()).setAccessorPermit(6)
                        .setAccessorXPermit(ExtendedPermitData.ALL));
        dmAcl = getEntityManager().merge(dmAcl);
        getEntityManager().flush();
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
