package pro.documentum.util.versions;

import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.IDfId;

import pro.documentum.junit.DfcTestSupport;
import pro.documentum.util.ids.DfIdUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class VersionTreeCopierTest extends DfcTestSupport {

    @Test
    public void testBasic() throws Exception {
        int count = 11;
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.setObjectName(RandomStringUtils.randomAscii(255));
        object.save();
        for (int i = 1; i < count; i++) {
            object.checkout();
            object.setObjectName(RandomStringUtils.randomAscii(255));
            object = (IDfSysObject) session.getObject(object.checkin(false,
                    null));
        }
        Map<IDfId, IDfId> map = VersionTreeCopier.copy(session,
                object.getObjectId(), false, false, false);
        assertEquals(count, map.size());
        for (Map.Entry<IDfId, IDfId> e : map.entrySet()) {
            assertEquals(
                    ((IDfSysObject) session.getObject(e.getKey()))
                            .getObjectName(),
                    ((IDfSysObject) session.getObject(e.getValue()))
                            .getObjectName());
        }
    }

    @Test
    public void testDeletedRoot() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        IDfId rootId = object.getObjectId();
        for (int i = 0; i < 10; i++) {
            object.checkout();
            object = (IDfSysObject) session.getObject(object.checkin(false,
                    null));
        }

        session.getObject(object.getChronicleId()).destroy();
        session.flushCache(true);

        Map<IDfId, IDfId> map = VersionTreeCopier.copy(session,
                object.getObjectId(), false, false, false);
        assertEquals(11, map.size());

        rootId = map.remove(rootId);
        for (IDfId objectId : map.values()) {
            session.getObject(objectId).destroy();
        }

        assertNull(session
                .getObjectByQualification("dm_sysobject WHERE r_object_id='"
                        + rootId.getId() + "'"));
    }

    @Test
    @Ignore
    public void testDeletedRootLocal() throws Exception {
        IDfSession session = getSession();
        Map<IDfId, IDfId> map = VersionTreeCopier.copy(session,
                DfIdUtil.getId("0902987880002cf1"), false, false, false);
        assertEquals(3, map.size());
    }

}
