package pro.documentum.persistence.jdo.model;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;

import pro.documentum.model.jdo.sysobject.DmFolder;
import pro.documentum.persistence.jdo.JDOTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class TestFetch extends JDOTestSupport {

    @Test
    public void testFolder1() throws Exception {
        IDfSession session = getUnderneathSession();
        IDfFolder folder = (IDfFolder) session.newObject("dm_folder");
        folder.setObjectName(RandomStringUtils.randomAlphabetic(10));
        folder.save();
        String id = folder.getObjectId().getId();
        DmFolder dmFolder = getPersistenceManager().getObjectById(
                DmFolder.class, id);
        assertNotNull(dmFolder);
    }

    @Test
    public void testFolder2() throws Exception {
        IDfSession session = getUnderneathSession();
        IDfFolder folder = (IDfFolder) session.newObject("dm_folder");
        folder.setObjectName(RandomStringUtils.randomAlphabetic(10));
        folder.save();
        String id = folder.getObjectId().getId();
        DmFolder dmFolder = (DmFolder) getPersistenceManager()
                .getObjectById(id);
        assertNotNull(dmFolder);
    }

}
