package pro.documentum.persistence.jdo.references;

import java.util.List;

import javax.jdo.Query;

import org.junit.Test;

import pro.documentum.model.jdo.DmFolder;
import pro.documentum.persistence.jdo.JDOTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class TestReferences extends JDOTestSupport {

    @Test
    public void testQueryByFolderPath() throws Exception {
        String path = "/System/Modules";
        Query query = getPersistenceManager().newQuery(DmFolder.class,
                "ANY(folderPaths == '" + path + "')");
        List<DmFolder> results = (List<DmFolder>) query.execute();
        assertNotNull(results);
        assertEquals(1, results.size());
        DmFolder folder = results.get(0);
        assertNotNull(folder.getFolders());
        assertNotNull(folder.getCabinet());
        assertNotNull(folder.getChronicle());
        assertEquals(folder, folder.getChronicle());
        assertTrue(folder.getFolderPaths().contains(path));
    }

}
