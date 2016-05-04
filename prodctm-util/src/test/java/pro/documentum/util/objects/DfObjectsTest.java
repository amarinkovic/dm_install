package pro.documentum.util.objects;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.documentum.fc.client.IDfSession;
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
        assertEquals(100, new HashSet<String>(Arrays.asList(ids)).size());
        for (String id : ids) {
            assertEquals(IDfId.DM_QUEUE_ITEM, DfId.valueOf(id).getTypePart());
            assertEquals(Long.valueOf(session.getDocbaseId()).longValue(), DfId
                    .valueOf(id).getNumericDocbaseId());
        }
    }

}
