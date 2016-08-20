package pro.documentum.persistence.jpa.query;

import static org.hamcrest.Matchers.endsWith;

import org.junit.Ignore;
import org.junit.Test;

import pro.documentum.model.jpa.DmFolder;
import pro.documentum.model.jpa.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AnyKeywordQueryTest extends AbstractQueryTest {

    @Test
    public void testAnyKeyword1() throws Exception {
        String q = str(jpa(DmFolder.class, "ANY(folderPaths "
                + "= '/System/Modules')"));
        assertThat(q, endsWith("WHERE "
                + "ANY (this.r_folder_path='/System/Modules')"));
    }

    @Test
    @Ignore
    public void testAnyKeyword2() throws Exception {
        String q = str(jpa(DmFolder.class, "ANY (folderPaths) "
                + "= '/System/Modules'"));
        assertThat(q, endsWith("WHERE "
                + "ANY this.r_folder_path='/System/Modules'"));
    }

    @Test
    @Ignore
    public void testAnyKeyword3() throws Exception {
        String q = str(jpa(DmFolder.class, "ANY (folderPaths) "
                + "= '/System/Modules' "
                + "OR ANY (folderIds = '/System/Modules')"));
        assertThat(q, endsWith("WHERE ("
                + "ANY this.r_folder_path='/System/Modules') "
                + "OR (ANY (this.i_folder_id='/System/Modules'))"));
    }

    @Test
    @Ignore
    public void testAnyKeyword4() throws Exception {
        String q = str(jpa(DmFolder.class, "ANY (folderPaths "
                + "= '/System/Modules' AND folderIds = '/System/Modules')"));
        assertThat(q, endsWith("WHERE "
                + "ANY ((this.r_folder_path='/System/Modules') "
                + "AND (this.i_folder_id='/System/Modules'))"));
    }

    @Test
    @Ignore
    public void testAnyKeyword5() throws Exception {
        String q = str(jpa(DmFolder.class, "ANY (folderPaths "
                + "= '/System/Modules' AND folderIds = '/System/Modules') "
                + "AND ANY (modifyDate = null)"));
        assertThat(q, endsWith("WHERE "
                + "(ANY ((this.r_folder_path='/System/Modules') "
                + "AND (this.i_folder_id='/System/Modules'))) "
                + "AND (ANY (this.r_modify_date IS NULL))"));
    }

    @Test
    @Ignore
    public void testAnyKeyword6() throws Exception {
        String q = str(jpa(DmFolder.class, "ANY (folderPaths "
                + "= '/System/Modules' AND folderIds = '/System/Modules') "
                + "AND ANY (modifyDate) = null"));
        assertThat(q, endsWith("WHERE "
                + "(ANY ((this.r_folder_path='/System/Modules') "
                + "AND (this.i_folder_id='/System/Modules'))) "
                + "AND (ANY this.r_modify_date IS NULL)"));
    }

    @Test
    public void testDateToString1() throws Exception {
        String q = str(jpa(DmUser.class, "ANY(DATETOSTRING(modifyDate,"
                + "'yyyy/mm/dd hh:mi:ss') = '2016/05/18 00:00:00')"));
        assertThat(q, endsWith("WHERE ANY (DATETOSTRING(this.r_modify_date,"
                + "'yyyy/mm/dd hh:mi:ss')='2016/05/18 00:00:00')"));
    }

    @Test
    public void testDateToString2() throws Exception {
        String q = str(jpa(DmUser.class, "ANY(DATETOSTRING(modifyDate,"
                + "'yyyy/mm/dd hh:mi:ss')) = '2016/05/18 00:00:00'"));
        assertThat(q, endsWith("WHERE ANY DATETOSTRING(this.r_modify_date,"
                + "'yyyy/mm/dd hh:mi:ss')='2016/05/18 00:00:00'"));
    }

}
