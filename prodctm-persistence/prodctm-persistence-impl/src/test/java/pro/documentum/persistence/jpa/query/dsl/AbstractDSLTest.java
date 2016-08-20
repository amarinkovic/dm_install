package pro.documentum.persistence.jpa.query.dsl;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;

import pro.documentum.persistence.jpa.query.AbstractQueryTest;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AbstractDSLTest extends AbstractQueryTest {

    protected JPAQueryFactory getQueryFactory() {
        return new JPAQueryFactory(new Provider<EntityManager>() {
            @Override
            public EntityManager get() {
                return getEntityManager();
            }
        });
    }

}
