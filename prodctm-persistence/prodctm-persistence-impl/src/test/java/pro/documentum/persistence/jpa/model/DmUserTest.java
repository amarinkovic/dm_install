package pro.documentum.persistence.jpa.model;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import pro.documentum.model.jpa.user.DmUser;
import pro.documentum.persistence.jpa.JPATestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DmUserTest extends JPATestSupport {

    @Test
    public void testCreate() throws Exception {
        String name = RandomStringUtils.randomAlphabetic(10);
        getEntityManager().persist(
                new DmUser().setUserName(name).setUserLoginName(name));
        getEntityManager().flush();
    }

}
