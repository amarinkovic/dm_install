package pro.documentum.persistence.jdo.query;

import static org.hamcrest.Matchers.endsWith;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import pro.documentum.model.jdo.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BooleanQueryTest extends AbstractQueryTest {

    @Test
    public void testBooleanTrue() throws Exception {
        String q = str(jdo(DmUser.class, "group == TRUE"));
        assertThat(q, endsWith("WHERE this.r_is_group=TRUE"));
    }

    @Test
    public void testBooleanFalse() throws Exception {
        String q = str(jdo(DmUser.class, "group == FALSE"));
        assertThat(q, endsWith("WHERE this.r_is_group=FALSE"));
    }

    @Test
    public void testBooleanTrueBinding() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("value", true);
        String q = str(jdo(DmUser.class, "group == :value"), params);
        assertThat(q, endsWith("WHERE this.r_is_group=TRUE"));
    }

    @Test
    public void testBooleanFalseBinding() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("value", false);
        String q = str(jdo(DmUser.class, "group == :value"), params);
        assertThat(q, endsWith("WHERE this.r_is_group=FALSE"));
    }
}
