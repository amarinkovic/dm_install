package pro.documentum.util.java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pro.documentum.util.java.decorators.BaseDecorator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DecoratorsTest {

    @Test
    public void testList() throws Exception {
        Object o = new Object();
        Object d1 = new BaseDecorator<>(o);
        Object d2 = new BaseDecorator<>(o);
        assertEquals(d1.hashCode(), d2.hashCode());
        assertEquals(d1, d2);
    }

}
