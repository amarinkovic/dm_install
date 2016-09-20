package pro.documentum.persistence.jdo.query.dsl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import pro.documentum.model.jdo.user.DmUser;
import pro.documentum.model.jdo.user.QDmUser;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DSLTest extends AbstractDSLTest {

    @Test
    public void testDSL() throws Exception {
        QDmUser dmUser = QDmUser.dmUser;
        List<DmUser> result = getQueryFactory().selectFrom(dmUser)
                .where(dmUser.userLoginName.eq(getLoginName())).fetch();
        assertNotNull(result);
        assertEquals(1, result.size());
        DmUser user = result.get(0);
        assertNotNull(user);
        assertEquals(getLoginName(), user.getUserLoginName());
        assertNotNull(user.getModifyDate());
    }

    @Test
    public void testDSL2() throws Exception {
        List<String> collection = new ArrayList<>();
        for (int i = 0; i < 999; i++) {
            collection.add(RandomStringUtils.randomAlphanumeric(32));
        }
        collection.add(null);
        collection.add(getLoginName());
        QDmUser dmUser = QDmUser.dmUser;
        List<DmUser> result = getQueryFactory().selectFrom(dmUser)
                .where(dmUser.userLoginName.in(collection)).fetch();
        assertNotNull(result);
        assertEquals(1, result.size());
        DmUser user = result.get(0);
        assertNotNull(user);
        assertEquals(getLoginName(), user.getUserLoginName());
        assertNotNull(user.getModifyDate());
    }

    @Test
    public void testDSL3() throws Exception {
        QDmUser dmUser = QDmUser.dmUser;
        List<DmUser> result = getQueryFactory().selectFrom(dmUser)
                .where(dmUser.userLoginName.isNull()).fetch();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

}
