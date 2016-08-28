package pro.documentum.demo.isis.dom.simple;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class SimpleObjectTest {

    SimpleObject simpleObject;

    @Before
    public void setUp() throws Exception {
        simpleObject = new SimpleObject();
    }

    public static class Name extends SimpleObjectTest {

        @Test
        public void happyCase() throws Exception {
            // given
            String name = "Foobar";
            assertThat(simpleObject.getObjectName()).isNull();

            // when
            simpleObject.setObjectName(name);

            // then
            assertThat(simpleObject.getObjectName()).isEqualTo(name);
        }
    }

}
