package pro.documentum.util.objects;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;

import pro.documentum.junit.DfcTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DfObjectsTest extends DfcTestSupport {

    @Test
    public void testMakeIds() throws Exception {
        IDfSession session = getSession();
        String[] ids = DfObjects.makeIds(session, "dmi_queue_item", 100);
        assertEquals(100, ids.length);
        assertEquals(100, new HashSet<>(Arrays.asList(ids)).size());
        for (String id : ids) {
            assertEquals(IDfId.DM_QUEUE_ITEM, DfId.valueOf(id).getTypePart());
            assertEquals(Long.valueOf(session.getDocbaseId()).longValue(), DfId
                    .valueOf(id).getNumericDocbaseId());
        }
    }

    @Test
    public void testLinkedToFolder() throws Exception {
        IDfSession session = getSession();
        String name1 = RandomStringUtils.randomAlphabetic(48);
        IDfFolder folder1 = (IDfFolder) session.newObject("dm_folder");
        folder1.setObjectName(name1);
        assertFalse(DfObjects.isLinkedToFolder(folder1, "/Temp"));
        folder1.link("/Temp");
        assertTrue(DfObjects.isLinkedToFolder(folder1, "/Temp"));
        String name2 = RandomStringUtils.randomAlphabetic(48);
        IDfFolder folder2 = (IDfFolder) session.newObject("dm_folder");
        folder2.setObjectName(name2);
        assertFalse(DfObjects.isLinkedToFolder(folder2, folder1.getObjectId()
                .getId()));
        folder2.link(folder1.getObjectId().getId());
        assertTrue(DfObjects.isLinkedToFolder(folder2, folder1.getObjectId()
                .getId()));
    }

    @Test
    public void testGetImp() throws Exception {
        IDfSession session = getSession();
        IDfSysObject proxy = (IDfSysObject) session.newObject("dm_document");
        IDfSysObject imp = DfObjects.getImp(proxy);
        assertNotNull(imp);
        assertNotEquals(proxy, imp);
        assertNotNull(DfObjects.getImp(imp));
        assertEquals(imp, DfObjects.getImp(imp));
    }

}
