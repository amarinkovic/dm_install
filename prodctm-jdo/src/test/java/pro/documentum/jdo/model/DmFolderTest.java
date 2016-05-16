package pro.documentum.jdo.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Query;

import org.junit.Test;

import pro.documentum.jdo.JDOTestSupport;
import pro.documentum.model.DmFolder;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DmFolderTest extends JDOTestSupport {

    @Test
    public void testQueryByFolderPath() throws Exception {
        String path = "/System/Modules";
        Query query = getPersistenceManager().newQuery(DmFolder.class,
                "ANY(folderPaths == '" + path + "')");
        List<DmFolder> results = (List<DmFolder>) query.execute();
        assertNotNull(results);
        assertEquals(1, results.size());
        DmFolder folder = results.get(0);
        assertTrue(folder.getFolderPaths().contains(path));
    }

    @Test
    public void testQueryByFolderPathBinding() throws Exception {
        String path = "/System/Modules";
        Query query = getPersistenceManager().newQuery(DmFolder.class,
                "ANY(folderPaths == :path)");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("path", path);
        List<DmFolder> results = (List<DmFolder>) query.executeWithMap(params);
        assertNotNull(results);
        assertEquals(1, results.size());
        DmFolder folder = results.get(0);
        assertTrue(folder.getFolderPaths().contains(path));
    }

}
