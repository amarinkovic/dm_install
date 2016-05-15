package pro.documentum.jdo.query;

import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

import pro.documentum.model.DmFolder;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AnyKeywordDQLMapperTest extends AbstractDQLMapperTest {

    @Test
    public void testAnyKeyword1() throws Exception {
        String q = newQuery(DmFolder.class,
                "ANY (folderPaths == '/System/Modules')");
        assertTrue(q
                .endsWith("WHERE ANY (this.r_folder_path='/System/Modules')"));
    }

    @Test
    public void testAnyKeyword2() throws Exception {
        String q = newQuery(DmFolder.class,
                "ANY (folderPaths) == '/System/Modules'");
        assertTrue(q.endsWith("WHERE ANY this.r_folder_path='/System/Modules'"));
    }

    @Test
    public void testAnyKeyword3() throws Exception {
        String q = newQuery(DmFolder.class,
                "ANY (folderPaths) == '/System/Modules' "
                        + "|| ANY (folderIds == '/System/Modules')");
        assertTrue(q
                .endsWith("WHERE (ANY this.r_folder_path='/System/Modules') "
                        + "OR (ANY (this.i_folder_id='/System/Modules'))"));
    }

    @Test
    public void testAnyKeyword4() throws Exception {
        String q = newQuery(DmFolder.class,
                "ANY (folderPaths == '/System/Modules' "
                        + "&& folderIds == '/System/Modules')");
        assertTrue(q
                .endsWith("WHERE ANY ((this.r_folder_path='/System/Modules') "
                        + "AND (this.i_folder_id='/System/Modules'))"));
    }

    @Test
    public void testAnyKeyword5() throws Exception {
        String q = newQuery(
                DmFolder.class,
                "ANY (folderPaths == '/System/Modules' "
                        + "&& folderIds == '/System/Modules') && ANY (modifyDate == null)");
        assertTrue(q
                .endsWith("WHERE (ANY ((this.r_folder_path='/System/Modules') "
                        + "AND (this.i_folder_id='/System/Modules'))) "
                        + "AND (ANY (this.r_modify_date IS NULL))"));
    }

    @Test
    public void testAnyKeyword6() throws Exception {
        String q = newQuery(
                DmFolder.class,
                "ANY (folderPaths == '/System/Modules' "
                        + "&& folderIds == '/System/Modules') && ANY (modifyDate) == null");
        assertTrue(q
                .endsWith("WHERE (ANY ((this.r_folder_path='/System/Modules') "
                        + "AND (this.i_folder_id='/System/Modules'))) "
                        + "AND (ANY this.r_modify_date IS NULL)"));
    }

}
