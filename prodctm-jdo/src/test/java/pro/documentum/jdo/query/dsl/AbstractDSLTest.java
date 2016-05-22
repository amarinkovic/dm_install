package pro.documentum.jdo.query.dsl;

import javax.inject.Provider;
import javax.jdo.PersistenceManager;

import com.querydsl.jdo.JDOQueryFactory;

import pro.documentum.jdo.query.AbstractQueryTest;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AbstractDSLTest extends AbstractQueryTest {

    protected JDOQueryFactory getQueryFactory() {
        return new JDOQueryFactory(new Provider<PersistenceManager>() {
            @Override
            public PersistenceManager get() {
                return getPersistenceManager();
            }
        });
    }

}
