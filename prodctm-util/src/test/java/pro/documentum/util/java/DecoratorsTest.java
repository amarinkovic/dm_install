package pro.documentum.util.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import pro.documentum.util.ISessionInvoker;
import pro.documentum.util.java.decorators.BaseDecorator;
import pro.documentum.util.java.decorators.cglib.CglibDecorators;
import pro.documentum.util.java.decorators.javassist.JavassistDecorators;
import pro.documentum.util.java.decorators.jdk.JDKDecorators;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DecoratorsTest {

    @Test
    public void testJavassistWrapEquals() throws Exception {
        Object o = new Object();
        Object d1 = JavassistDecorators.wrap(new BaseDecorator<>(o));
        Object d2 = JavassistDecorators.wrap(new BaseDecorator<>(o));
        assertEquals(d1.hashCode(), d2.hashCode());
        assertEquals(d1, d1);
        assertEquals(d2, d2);
        assertNotEquals(d1, d2);
        assertNotEquals(d2, d1);
    }

    @Test
    public void testCglibWrapEquals() throws Exception {
        Object o = new Object();
        Object d1 = CglibDecorators.wrap(new BaseDecorator<>(o));
        Object d2 = CglibDecorators.wrap(new BaseDecorator<>(o));
        assertEquals(d1.hashCode(), d2.hashCode());
        assertEquals(d1, d1);
        assertEquals(d2, d2);
        assertNotEquals(d1, d2);
        assertNotEquals(d2, d1);
    }

    @Test
    public void testProxyEquals() throws Exception {
        Object o = new Object();
        Object d1 = JDKDecorators.proxy(new BaseDecorator<>(o));
        Object d2 = JDKDecorators.proxy(new BaseDecorator<>(o));
        assertEquals(d1.hashCode(), d2.hashCode());
        assertEquals(d1, d1);
        assertEquals(d2, d2);
        assertNotEquals(d1, d2);
        assertNotEquals(d2, d1);
    }

    @Test
    public void testMixedEquals() throws Exception {
        Object o = new Object();
        Object d1 = JavassistDecorators.wrap(new BaseDecorator<>(o));
        Object d2 = JDKDecorators.proxy(new BaseDecorator<>(o));
        Object d3 = CglibDecorators.wrap(new BaseDecorator<>(o));
        assertEquals(d1.hashCode(), d2.hashCode());
        assertEquals(d1.hashCode(), d3.hashCode());
        assertEquals(d1, d1);
        assertEquals(d2, d2);
        assertEquals(d3, d3);
        assertNotEquals(d1, d2);
        assertNotEquals(d2, d1);
        assertNotEquals(d1, d3);
        assertNotEquals(d3, d1);
    }

    @Test
    public void testJavassistNoDefaultConstructor() throws Exception {
        final String rnd = RandomStringUtils.random(10);
        TestNDC testNDC = new TestNDC("xxxx");
        TestNDC ndc = JavassistDecorators.wrap(new BaseDecorator<TestNDC>(
                testNDC) {

            public String getXXX() {
                return rnd;
            }

        });

        assertEquals(rnd, ndc.getXXX());
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testCglibNoDefaultConstructor() throws Exception {
        final String rnd = RandomStringUtils.random(10);
        TestNDC testNDC = new TestNDC("xxxx");
        TestNDC ndc = CglibDecorators.wrap(new BaseDecorator<TestNDC>(testNDC) {

            public String getXXX() {
                return rnd;
            }

        });

        assertEquals(rnd, ndc.getXXX());
    }

    @Test
    public void testProxy() throws Exception {
        final String rnd = RandomStringUtils.random(10);

        final ISessionInvoker<String, Object, Exception> invoker = new ISessionInvoker<String, Object, Exception>() {
            @Override
            public String invoke(Object session) throws Exception {
                return "xxxx";
            }
        };

        ISessionInvoker<String, Object, Exception> proxy = JDKDecorators
                .proxy(new BaseDecorator<ISessionInvoker<String, Object, Exception>>(
                        invoker) {

                    public String invoke(Object session) throws Exception {
                        return rnd;
                    }

                });

        assertEquals(rnd, proxy.invoke(null));

    }

    class TestNDC {

        private final String _xxx;

        public TestNDC(String xxx) {
            _xxx = xxx;
        }

        public String getXXX() {
            return _xxx;
        }

    }

}
