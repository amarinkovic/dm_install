package pro.documentum.demo.isis.dom.simple;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.Lists;

public class SimpleObjectsTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2
            .createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    RepositoryService mockRepositoryService;

    SimpleObjects simpleObjects;

    @Before
    public void setUp() throws Exception {
        simpleObjects = new SimpleObjects();
        simpleObjects.repositoryService = mockRepositoryService;
    }

    public static class Create extends SimpleObjectsTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final SimpleObject simpleObject = new SimpleObject();

            final Sequence seq = context.sequence("create");
            context.checking(new Expectations() {
                {
                    oneOf(mockRepositoryService)
                            .instantiate(SimpleObject.class);
                    inSequence(seq);
                    will(returnValue(simpleObject));

                    oneOf(mockRepositoryService).persist(simpleObject);
                    inSequence(seq);
                }
            });

            // when
            final SimpleObject obj = simpleObjects.create("Foobar");

            // then
            assertThat(obj).isEqualTo(simpleObject);
            assertThat(obj.getObjectName()).isEqualTo("Foobar");
        }

    }

    public static class ListAll extends SimpleObjectsTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<SimpleObject> all = Lists.newArrayList();

            context.checking(new Expectations() {
                {
                    oneOf(mockRepositoryService).allInstances(
                            SimpleObject.class);
                    will(returnValue(all));
                }
            });

            // when
            final List<SimpleObject> list = simpleObjects.listAll();

            // then
            assertThat(list).isEqualTo(all);
        }
    }
}
