package pro.documentum.util.objects.changes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.client.impl.ISysObject;

import pro.documentum.junit.DfcTestSupport;
import pro.documentum.util.objects.DfObjects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ChangesProcessorTest extends DfcTestSupport {

    @Test
    public void testVstamp() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        assertEquals(0, object.getVStamp());
        object.checkout();
        assertEquals(1, object.getVStamp());
        object.cancelCheckout();
        assertEquals(2, object.getVStamp());
    }

    @Test
    public void testRepeatingString() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        Map<String, Object> data = new HashMap<>();
        List<String> authors = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            authors.add(RandomStringUtils.randomAlphabetic(48));
        }
        data.put("authors", authors);
        ChangesProcessor.process(object, data);
        assertEquals(authors.size(), object.getValueCount("authors"));
    }

    @Test
    public void testLockOwner() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        Map<String, Object> data = new HashMap<>();
        data.put("r_lock_owner", session.getLoginUserName());
        ChangesProcessor.process(object, data);
        assertTrue(object.isCheckedOutBy(null));
        assertFalse(object.isDirty());
        data.put("r_lock_owner", null);
        ChangesProcessor.process(object, data);
        assertFalse(object.isCheckedOut());
        assertFalse(object.isDirty());
    }

    @Test
    public void testCheckIn1() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        Map<String, Object> data = new HashMap<>();
        data.put("r_lock_owner", session.getLoginUserName());
        ChangesProcessor.process(object, data);
        assertTrue(object.isCheckedOutBy(null));
        assertFalse(object.isDirty());
        data.put("r_version_label", Arrays.asList("1.1,CURRENT"));
        data.put("r_lock_owner", null);
        ChangesProcessor.process(object, data);
        assertFalse(object.isCheckedOut());
        assertFalse(object.isDirty());
        assertFalse(object.getHasFolder());
        assertEquals("1.0", object.getVersionLabels().getImplicitVersionLabel());
    }

    @Test
    public void testCheckIn2() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        Map<String, Object> data = new HashMap<>();
        data.put("r_lock_owner", session.getLoginUserName());
        ChangesProcessor.process(object, data);
        assertTrue(object.isCheckedOutBy(null));
        assertFalse(object.isDirty());
        String objectName = RandomStringUtils.randomAlphabetic(48);
        data.put("r_version_label", Arrays.asList("1.1,CURRENT"));
        data.put("object_name", objectName);
        ChangesProcessor.process(object, data);
        assertFalse(object.isDirty());
        assertFalse(object.getHasFolder());
        assertFalse(object.isCheckedOut());
        assertEquals("1.0", object.getVersionLabels().getImplicitVersionLabel());
        assertNotEquals(object, object.getObjectName());
        object = (IDfSysObject) session
                .getObjectByQualification("dm_document where i_chronicle_id='"
                        + object.getChronicleId() + "'");
        assertTrue(object.isCheckedOut());
        assertEquals(objectName, object.getObjectName());
        assertEquals("1.1", object.getVersionLabels().getImplicitVersionLabel());
    }

    @Test
    public void testCheckIn3() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        Map<String, Object> data = new HashMap<>();
        data.put("r_lock_owner", session.getLoginUserName());
        ChangesProcessor.process(object, data);
        assertTrue(object.isCheckedOutBy(null));
        assertFalse(object.isDirty());
        String objectName = RandomStringUtils.randomAlphabetic(48);
        data.put("r_version_label", Arrays.asList("2.0,CURRENT"));
        data.put("object_name", objectName);
        ChangesProcessor.process(object, data);
        assertFalse(object.isDirty());
        assertFalse(object.getHasFolder());
        assertFalse(object.isCheckedOut());
        assertEquals("1.0", object.getVersionLabels().getImplicitVersionLabel());
        assertNotEquals(object, object.getObjectName());
        object = (IDfSysObject) session
                .getObjectByQualification("dm_document where i_chronicle_id='"
                        + object.getChronicleId() + "'");
        assertTrue(object.isCheckedOut());
        assertEquals(objectName, object.getObjectName());
        assertEquals("2.0", object.getVersionLabels().getImplicitVersionLabel());
    }

    @Test
    public void testLockOwnerDependency() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        Map<String, Object> data = new HashMap<>();
        List<String> authors = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            authors.add(RandomStringUtils.randomAlphabetic(48));
        }
        data.put("authors", authors);
        data.put("r_lock_owner", session.getLoginUserName());
        ChangesProcessor.process(object, data);
        assertTrue(object.isCheckedOutBy(null));
        assertEquals(authors.size(), object.getValueCount("authors"));
    }

    @Test
    public void testLinkToFolder() throws Exception {
        IDfSession session = getSession();
        IDfFolder temp = session.getFolderByPath("/Temp");
        IDfFolder folder1 = (IDfFolder) session.newObject("dm_folder");
        assertFalse(DfObjects.isLinkedToFolder(folder1, "/Temp"));
        Map<String, Object> changes = new HashMap<>();
        String name1 = RandomStringUtils.randomAlphabetic(48);
        changes.put("object_name", name1);
        changes.put("i_folder_id", Arrays.asList(temp.getObjectId().getId()));
        ChangesProcessor.process(folder1, changes);
        assertEquals(name1, folder1.getObjectName());
        assertTrue(DfObjects.isLinkedToFolder(folder1, "/Temp"));
        assertEquals(0, folder1.getValueCount("i_folder_id"));
        assertEquals(0, folder1.getFolderIdCount());
        assertEquals(1, ((ISysObject) folder1).getFolderIdCountEx());
    }

    @Test
    public void testReadOnly() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        Date date = new Date();
        Map<String, Object> changes = new HashMap<>();
        changes.put("r_creation_date", date);
        changes.put("r_modify_date", date);
        ChangesProcessor.process(object, changes);
        assertNotEquals(date, object.getCreationDate().getDate());
        assertNotEquals(date, object.getModifyDate().getDate());
    }

    @Test
    public void testUserPermit() throws Exception {
        IDfSession session = getSession();
        String userName = RandomStringUtils.randomAlphabetic(48);
        IDfUser object = (IDfUser) session.newObject("dm_user");
        Map<String, Object> changes = new HashMap<>();
        changes.put("owner_def_permit", 0);
        changes.put("group_def_permit", 0);
        changes.put("world_def_permit", 0);
        changes.put("user_name", userName);
        changes.put("user_login_name", userName);
        ChangesProcessor.process(object, changes);
        object.save();
    }
}
