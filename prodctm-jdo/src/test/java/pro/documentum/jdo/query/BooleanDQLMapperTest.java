package pro.documentum.jdo.query;

import static junit.framework.TestCase.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import pro.documentum.model.DmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BooleanDQLMapperTest extends AbstractDQLMapperTest {

    @Test
    public void testBooleanTrue() throws Exception {
        String q = newQuery(DmUser.class, "group == TRUE");
        assertTrue(q.endsWith("WHERE this.r_is_group=TRUE"));
    }

    @Test
    public void testBooleanFalse() throws Exception {
        String q = newQuery(DmUser.class, "group == FALSE");
        assertTrue(q.endsWith("WHERE this.r_is_group=FALSE"));
    }

    @Test
    public void testBooleanTrueBinding() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("value", true);
        String q = newQuery(DmUser.class, "group == :value", params);
        assertTrue(q.endsWith("WHERE this.r_is_group=TRUE"));
    }

    @Test
    public void testBooleanFalseBinding() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("value", false);
        String q = newQuery(DmUser.class, "group == :value", params);
        assertTrue(q.endsWith("WHERE this.r_is_group=FALSE"));
    }

}
