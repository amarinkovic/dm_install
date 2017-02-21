package pro.documentum.util.objects;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfQueueItem;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.IDfId;

import pro.documentum.junit.DfcTestSupport;
import pro.documentum.util.ids.DfIdUtil;
import pro.documentum.util.queries.Queries;

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
            assertEquals(IDfId.DM_QUEUE_ITEM, DfIdUtil.getId(id).getTypePart());
            assertEquals(Long.valueOf(session.getDocbaseId()).longValue(),
                    DfIdUtil.getId(id).getNumericDocbaseId());
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

    @Test
    public void testGuessingType1() throws Exception {
        IDfSession session = getSession();
        String query = "SELECT r_object_id, i_vstamp, i_is_replica FROM dm_acl";
        IDfTypedObject data = Queries.execute(session, query).next();
        IDfPersistentObject object = DfObjects.asPersistent(session, data, null);
        assertTrue(object instanceof IDfACL);
    }

    @Test
    public void testGuessingType2() throws Exception {
        IDfSession session = getSession();
        // todo: make source_docbase mandatory when building query
        String query = "SELECT r_object_id, i_vstamp, i_is_replica, source_docbase FROM dmi_queue_item";
        IDfTypedObject data = Queries.execute(session, query).next();
        IDfPersistentObject object = DfObjects.asPersistent(session, data, null);
        assertTrue(object instanceof IDfQueueItem);
    }

    @Test
    public void testGuessingType3() throws Exception {
        IDfSession session = getSession();
        String query = "SELECT r_object_id, i_vstamp, r_object_type, i_is_replica, i_is_reference, r_aspect_name FROM dm_server_config";
        IDfTypedObject data = Queries.execute(session, query).next();
        IDfPersistentObject object = DfObjects.asPersistent(session, data, null);
        assertTrue(StringUtils.isNotBlank(object.getString("object_name")));
    }

    @Test
    public void testResetAcl() throws Exception {
        IDfACL object = (IDfACL) getSession().newObject("dm_acl");
        object.setObjectName(RandomStringUtils.randomAlphabetic(32));
        object.setDomain(getLoginName());
        object.grant(getLoginName(), 7, null);
        object.save();
        assertEquals(3, object.getAccessorCount());
        DfObjects.resetAcl(object);
        assertEquals(2, object.getAccessorCount());
        object.save();
        assertEquals(2, object.getAccessorCount());
    }

}
