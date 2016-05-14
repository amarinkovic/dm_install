package pro.documentum.util.objects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.content.IDfContent;

import pro.documentum.junit.DfcTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DfContentsTest extends DfcTestSupport {

    @Test
    public void testCreateStream() throws Exception {
        byte[] bytes = RandomStringUtils.random(2000).getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        IDfSession session = getSession();
        IDfContent content = DfContents.create(session, bais, null,
                "dm_document", "crtext");
        assertTrue(content.isDirty());
        assertTrue(content.isNew());
        DfContents.save(content);
        assertFalse(content.isDirty());
        assertFalse(content.isNew());
        assertEquals(bytes.length, content.getContentSize());
    }

    @Test
    public void testLink() throws Exception {
        IDfSession session = getSession();
        IDfSysObject object = (IDfSysObject) session.newObject("dm_document");
        object.save();
        assertFalse(object.isDirty());
        assertFalse(object.isNew());
        byte[] bytes = RandomStringUtils.random(2000).getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        IDfContent content = DfContents.create(session, bais, null,
                "dm_document", "crtext");
        DfObjects.link(content, object, 0, null);
        DfObjects.setPrimary(object, content);
        DfContents.save(content, object);
        assertTrue(object.isDirty());
        object.save();
        assertEquals(bytes.length, object.getContentSize());
        ByteArrayInputStream obais = object.getContentEx2(content
                .getFullFormat(), 0, null);
        ByteArrayOutputStream obaos = new ByteArrayOutputStream();
        IOUtils.copy(obais, obaos);
        assertArrayEquals(bytes, obaos.toByteArray());
    }

    @Test
    public void testUnknownFormat() throws Exception {
        byte[] bytes = RandomStringUtils.random(2000).getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        IDfSession session = getSession();
        IDfContent content = DfContents.create(session, bais, null,
                "dm_document", RandomStringUtils.random(20));
        assertTrue(content.isDirty());
        assertTrue(content.isNew());
        DfContents.save(content);
        assertFalse(content.isDirty());
        assertFalse(content.isNew());
        assertEquals("unknown", content.getFullFormat());
    }

}
