package pro.documentum.persistence.jpa.query;

import static org.hamcrest.Matchers.endsWith;

import org.junit.Test;

import pro.documentum.model.jpa.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PartialCompilationTest extends AbstractQueryTest {

    @Test
    public void testSimple1() throws Exception {
        String q = str(jpa(DmUser.class, "userName.toWhatEver() "
                + "= userLoginName"));
        assertThat(q, endsWith("WHERE 1=1"));
    }

    @Test
    public void testSimple2() throws Exception {
        String q = str(jpa(DmUser.class, "userName.toWhatEver() "
                + "<> userLoginName"));
        assertThat(q, endsWith("WHERE 1=1"));
    }

    @Test
    public void testNot1() throws Exception {
        String q = str(jpa(DmUser.class, "NOT (userName.toWhatEver() "
                + "= userLoginName)"));
        assertThat(q, endsWith("WHERE NOT (1<>1)"));
    }

    @Test
    public void testNot2() throws Exception {
        String q = str(jpa(DmUser.class, "NOT (userName.toWhatEver() "
                + "<> userLoginName)"));
        assertThat(q, endsWith("WHERE NOT (1<>1)"));
    }

    @Test
    public void testNot3() throws Exception {
        String q = str(jpa(DmUser.class, "NOT (NOT "
                + "(userName.toWhatEver() = userLoginName))"));
        assertThat(q, endsWith("WHERE NOT (NOT (1=1))"));
    }

    @Test
    public void testNot4() throws Exception {
        String q = str(jpa(DmUser.class, "NOT (NOT (NOT "
                + "(userName.toWhatEver() = userLoginName)))"));
        assertThat(q, endsWith("WHERE NOT (NOT (NOT (1<>1)))"));
    }

    @Test
    public void testNot5() throws Exception {
        String q = str(jpa(DmUser.class, "NOT (userName.toWhatEver() "
                + "= userLoginName AND userName = userLoginName)"));
        assertThat(q, endsWith("WHERE NOT ((1<>1) "
                + "AND (this.user_name=this.user_login_name))"));
    }

    @Test
    public void testNot6() throws Exception {
        String q = str(jpa(DmUser.class, "NOT (userName.toWhatEver() "
                + "= userLoginName) AND userName = userLoginName"));
        assertThat(q, endsWith("WHERE (NOT (1<>1)) "
                + "AND (this.user_name=this.user_login_name)"));
    }

}
