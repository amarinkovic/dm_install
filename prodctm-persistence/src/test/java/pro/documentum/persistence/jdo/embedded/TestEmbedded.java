package pro.documentum.persistence.jdo.embedded;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.documentum.fc.client.IDfSysObject;

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
    @Ignore
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

        DmAcl acl = dmSysobject.getAcl();
        acl = getPersistenceManager().detachCopy(acl);
        acl.getPermits().add(new DmPermit());
        acl = getPersistenceManager().makePersistent(acl);
        getPersistenceManager().flush();

        assertFalse(object.isCheckedOut());

    }

}
