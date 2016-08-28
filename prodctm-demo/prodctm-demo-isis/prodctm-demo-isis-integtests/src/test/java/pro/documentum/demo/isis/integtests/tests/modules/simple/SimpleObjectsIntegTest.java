package pro.documentum.demo.isis.integtests.tests.modules.simple;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Throwables;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import pro.documentum.demo.isis.dom.simple.SimpleObject;
import pro.documentum.demo.isis.dom.simple.SimpleObjects;
import pro.documentum.demo.isis.fixture.dom.simple.SimpleObjectsTearDown;
import pro.documentum.demo.isis.fixture.scenarios.RecreateSimpleObjects;
import pro.documentum.demo.isis.integtests.tests.DomainAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleObjectsIntegTest extends DomainAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;
    @Inject
    SimpleObjects simpleObjects;

    public static class ListAll extends SimpleObjectsIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            RecreateSimpleObjects fs = new RecreateSimpleObjects();
            fixtureScripts.runFixtureScript(fs, null);
            nextTransaction();

            // when
            final List<SimpleObject> all = wrap(simpleObjects).listAll();

            // then
            assertThat(all).hasSize(fs.getSimpleObjects().size());

            SimpleObject simpleObject = wrap(all.get(0));
            assertThat(simpleObject.getName()).isEqualTo(fs.getSimpleObjects().get(0).getName());
        }

        @Test
        public void whenNone() throws Exception {

            // given
            FixtureScript fs = new SimpleObjectsTearDown();
            fixtureScripts.runFixtureScript(fs, null);
            nextTransaction();

            // when
            final List<SimpleObject> all = wrap(simpleObjects).listAll();

            // then
            assertThat(all).hasSize(0);
        }
    }

    public static class Create extends SimpleObjectsIntegTest {

        @Test
        public void happyCase() throws Exception {

            // given
            FixtureScript fs = new SimpleObjectsTearDown();
            fixtureScripts.runFixtureScript(fs, null);
            nextTransaction();

            // when
            wrap(simpleObjects).create("Faz");

            // then
            final List<SimpleObject> all = wrap(simpleObjects).listAll();
            assertThat(all).hasSize(1);
        }

        @Test
        public void whenAlreadyExists() throws Exception {

            // given
            FixtureScript fs = new SimpleObjectsTearDown();
            fixtureScripts.runFixtureScript(fs, null);
            nextTransaction();
            wrap(simpleObjects).create("Faz");
            nextTransaction();

            // then
            expectedExceptions.expectCause(causalChainContains(SQLIntegrityConstraintViolationException.class));

            // when
            wrap(simpleObjects).create("Faz");
            nextTransaction();
        }

        private static Matcher<? extends Throwable> causalChainContains(final Class<?> cls) {
            return new TypeSafeMatcher<Throwable>() {
                @Override
                protected boolean matchesSafely(Throwable item) {
                    final List<Throwable> causalChain = Throwables.getCausalChain(item);
                    for (Throwable throwable : causalChain) {
                        if(cls.isAssignableFrom(throwable.getClass())){
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public void describeTo(Description description) {
                    description.appendText("exception with causal chain containing " + cls.getSimpleName());
                }
            };
        }
    }

}