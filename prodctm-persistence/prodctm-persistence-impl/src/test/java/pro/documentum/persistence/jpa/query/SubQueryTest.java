package pro.documentum.persistence.jpa.query;

import static org.hamcrest.Matchers.endsWith;

import org.junit.Ignore;
import org.junit.Test;

import pro.documentum.model.jpa.sysobject.DmFolder;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class SubQueryTest extends AbstractQueryTest {

    @Test
    public void testSubquery1() throws Exception {
        String q = str(jpql(DmFolder.class,
                "objectId IN (select objectId from "
                        + "pro.documentum.model.jpa.sysobject.DmFolder subq)"));
        assertThat(q, endsWith("WHERE this.r_object_id IN "
                + "(SELECT subq.r_object_id FROM dm_folder subq)"));
    }

    @Test
    public void testSubquery11() throws Exception {
        String q = str(jpql(DmFolder.class,
                "objectId NOT IN (select objectId from "
                        + "pro.documentum.model.jpa.sysobject.DmFolder subq)"));
        assertThat(q, endsWith("WHERE NOT (this.r_object_id IN "
                + "(SELECT subq.r_object_id FROM dm_folder subq))"));
    }

    @Test
    public void testSubquery2() throws Exception {
        String q = str(jpql(DmFolder.class,
                "objectId IN (select objectId from "
                        + "pro.documentum.model.jpa.sysobject.DmFolder subq)"
                        + " AND objectId <> NULL"));
        assertThat(q, endsWith("WHERE (this.r_object_id IN "
                + "(SELECT subq.r_object_id FROM dm_folder subq)) "
                + "AND (this.r_object_id IS NOT NULL)"));
    }

    @Test
    public void testSubquery21() throws Exception {
        String q = str(jpql(DmFolder.class,
                "objectId NOT IN (select objectId from "
                        + "pro.documentum.model.jpa.sysobject.DmFolder subq)"
                        + " AND objectId <> NULL"));
        assertThat(q, endsWith("WHERE (NOT (this.r_object_id IN (SELECT "
                + "subq.r_object_id FROM dm_folder subq))) "
                + "AND (this.r_object_id IS NOT NULL)"));
    }

    @Test
    public void testSubquery3() throws Exception {
        String q = str(jpql(DmFolder.class,
                "objectId IN (select objectId from "
                        + "pro.documentum.model.jpa.sysobject.DmFolder subq"
                        + " WHERE subq.objectId=this.objectId)"));
        assertThat(q, endsWith("WHERE this.r_object_id IN "
                + "(SELECT subq.r_object_id FROM dm_folder subq "
                + "WHERE subq.r_object_id=this.r_object_id)"));
    }

    @Test
    public void testSubquery31() throws Exception {
        String q = str(jpql(DmFolder.class,
                "objectId NOT IN (select objectId from "
                        + "pro.documentum.model.jpa.sysobject.DmFolder subq"
                        + " WHERE subq.objectId=this.objectId)"));
        assertThat(q, endsWith("WHERE NOT (this.r_object_id IN ("
                + "SELECT subq.r_object_id FROM dm_folder subq "
                + "WHERE subq.r_object_id=this.r_object_id))"));
    }

    @Test
    public void testSubquery4() throws Exception {
        String q = str(jpql(DmFolder.class,
                "objectId <> NULL AND EXISTS (select objectId from "
                        + "pro.documentum.model.jpa.sysobject.DmFolder subq)"));
        assertThat(q, endsWith("WHERE (this.r_object_id IS NOT NULL) "
                + "AND (EXISTS (SELECT subq.r_object_id FROM dm_folder subq))"));
    }

    @Test
    public void testSubquery41() throws Exception {
        String q = str(jpql(DmFolder.class,
                "objectId <> NULL AND NOT EXISTS (select objectId from "
                        + "pro.documentum.model.jpa.sysobject.DmFolder subq)"));
        assertThat(q, endsWith("WHERE (this.r_object_id IS NOT NULL) "
                + "AND (NOT (EXISTS (SELECT subq.r_object_id "
                + "FROM dm_folder subq)))"));
    }

    @Test
    public void testSubquery5() throws Exception {
        String q = str(jpql(DmFolder.class,
                "objectId <> NULL AND EXISTS (select objectId from "
                        + "pro.documentum.model.jpa.sysobject.DmFolder subq"
                        + " WHERE subq.objectId = this.objectId)"));
        assertThat(q, endsWith("WHERE (this.r_object_id IS NOT NULL) "
                + "AND (EXISTS (SELECT subq.r_object_id FROM dm_folder subq "
                + "WHERE subq.r_object_id=this.r_object_id))"));
    }

    @Test
    public void testSubquery51() throws Exception {
        String q = str(jpql(DmFolder.class,
                "objectId <> NULL AND NOT EXISTS (select objectId from "
                        + "pro.documentum.model.jpa.sysobject.DmFolder subq"
                        + " WHERE subq.objectId = this.objectId)"));
        assertThat(q, endsWith("WHERE (this.r_object_id IS NOT NULL) AND (NOT "
                + "(EXISTS (SELECT subq.r_object_id FROM dm_folder subq "
                + "WHERE subq.r_object_id=this.r_object_id)))"));
    }

    @Test
    @Ignore
    public void testSubquery6() throws Exception {
        String q = str(jpql(DmFolder.class, "EXISTS (select objectId from "
                + "pro.documentum.model.jpa.sysobject.DmFolder subq) "
                + "AND objectId <> NULL"));
        assertThat(q, endsWith("WHERE (EXISTS (SELECT subq.r_object_id "
                + "FROM dm_folder subq)) AND (this.r_object_id IS NOT NULL)"));
    }

    @Test
    public void testSubquery7() throws Exception {
        String q = str(jpql(DmFolder.class, "(EXISTS (select objectId from "
                + "pro.documentum.model.jpa.sysobject.DmFolder subq)) "
                + "AND objectId <> NULL"));
        assertThat(q, endsWith("WHERE (EXISTS (SELECT subq.r_object_id "
                + "FROM dm_folder subq)) AND (this.r_object_id IS NOT NULL)"));
    }

}
