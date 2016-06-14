package pro.documentum.util.queries.bulk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfQueueItem;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.IDfId;

import pro.documentum.junit.DfcTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BulkIdentityIteratorTest extends DfcTestSupport {

    @Test
    public void testSingleSysObject() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        List<String> ids = Collections.singletonList(object.getObjectId()
                .getId());
        Iterator<IDfSysObject> iterator = new BulkIdentityIterator<>(session,
                "dm_document", ids);
        assertTrue(iterator.hasNext());
        assertEquals(object, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSingleSysObjectFlush() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        session.flushObject(object.getObjectId());
        List<String> ids = Collections.singletonList(object.getObjectId()
                .getId());
        Iterator<IDfSysObject> iterator = new BulkIdentityIterator<>(session,
                "dm_document", ids);
        assertTrue(iterator.hasNext());
        assertNotEquals(object, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSingleSysObjectDirty() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        session.flushObject(object.getObjectId());
        List<String> ids = Collections.singletonList(object.getObjectId()
                .getId());
        Iterator<IDfSysObject> iterator = new BulkIdentityIterator<>(session,
                "dm_document", ids);
        object = (IDfSysObject) session.getObject(object.getObjectId());
        object.setObjectName("test");
        assertTrue(iterator.hasNext());
        assertEquals(object, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSingleSysObjectDestroy() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        List<String> ids = Collections.singletonList(object.getObjectId()
                .getId());
        Iterator<IDfSysObject> iterator = new BulkIdentityIterator<>(session,
                "dm_document", ids);
        object.destroy();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSingleSysObjectRepeatings() throws Exception {
        List<String> authors = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            authors.add(RandomStringUtils.randomAlphabetic(48));
        }
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        for (String author : authors) {
            object.appendString("authors", author);
        }
        object.save();
        session.flushObject(object.getObjectId());
        List<String> ids = Collections.singletonList(object.getObjectId()
                .getId());
        Iterator<IDfSysObject> iterator = new BulkIdentityIterator<>(session,
                "dm_document", ids);
        assertTrue(iterator.hasNext());
        object = iterator.next();
        assertEquals(authors.size(), object.getValueCount("authors"));
        for (int i = 0, n = object.getValueCount("authors"); i < n; i++) {
            assertEquals(authors.get(i),
                    object.getRepeatingString("authors", i));
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSingleSysQueueItem() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        IDfId queueItemId = object.queue(session.getLoginUserName(), "event",
                0, false, null, null);
        IDfQueueItem queueItem = (IDfQueueItem) session.getObject(queueItemId);
        List<String> ids = Collections.singletonList(queueItemId.getId());
        Iterator<IDfQueueItem> iterator = new BulkIdentityIterator<>(session,
                "dmi_queue_item", ids);
        assertTrue(iterator.hasNext());
        assertEquals(queueItem, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testMultipleSysObjects() throws Exception {
        IDfSession session = getSession();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            IDfSysObject object = (IDfSysObject) session
                    .newObject("dm_document");
            object.save();
            ids.add(object.getObjectId().getId());
        }
        Iterator<IDfSysObject> iterator = new BulkIdentityIterator<>(session,
                "dm_document", new ArrayList<>(ids));
        for (int i = 0; i < 500; i++) {
            assertTrue(iterator.hasNext());
            assertTrue(ids.remove(iterator.next().getObjectId().getId()));
        }
        assertTrue(ids.isEmpty());
    }

}
