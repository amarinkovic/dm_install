package pro.documentum.util.queries.bulk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BulkResultIteratorTest {

    @Test
    public void test1() throws Exception {
        List<String> keys = Arrays.asList("1", "2", "3");
        Map<String, List<String>> result = new HashMap<>();
        Iterator<String> iterator = new BulkResultIterator<>(keys, result);
        assertTrue(iterator.hasNext());
        assertNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertNull(iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test2() throws Exception {
        List<String> keys = Arrays.asList("1", "2", "3");
        Map<String, List<String>> result = new HashMap<>();
        result.put("1", new ArrayList<>(Arrays.asList("1")));
        result.put("2", new ArrayList<>(Arrays.asList("2")));
        result.put("3", new ArrayList<>(Arrays.asList("3")));
        Iterator<String> iterator = new BulkResultIterator<>(keys, result);
        assertTrue(iterator.hasNext());
        assertEquals("1", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("2", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("3", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test3() throws Exception {
        List<String> keys = Arrays.asList("1", "2", "3");
        Map<String, List<String>> result = new HashMap<>();
        result.put("1", new ArrayList<>(Arrays.asList("1", "10")));
        result.put("2", new ArrayList<>(Arrays.asList("2")));
        result.put("3", new ArrayList<>(Arrays.asList("3")));
        Iterator<String> iterator = new BulkResultIterator<>(keys, result);
        assertTrue(iterator.hasNext());
        assertEquals("1", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("10", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("2", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("3", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test4() throws Exception {
        List<String> keys = Arrays.asList("1", "2", "3");
        Map<String, List<String>> result = new HashMap<>();
        result.put("1", new ArrayList<>(Arrays.asList("1")));
        result.put("2", new ArrayList<>(Arrays.asList("2")));
        result.put("3", new ArrayList<>(Arrays.asList("3", "30")));
        Iterator<String> iterator = new BulkResultIterator<>(keys, result);
        assertTrue(iterator.hasNext());
        assertEquals("1", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("2", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("3", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("30", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test5() throws Exception {
        List<String> keys = Arrays.asList("1", "2", "3");
        Map<String, List<String>> result = new HashMap<>();
        result.put("1", new ArrayList<String>());
        result.put("2", new ArrayList<String>());
        result.put("3", new ArrayList<String>());
        Iterator<String> iterator = new BulkResultIterator<>(keys, result);
        assertTrue(iterator.hasNext());
        assertNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertNull(iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test6() throws Exception {
        List<String> keys = Arrays.asList("1", "2", "3");
        Map<String, List<String>> result = new HashMap<>();
        result.put("1", new ArrayList<>(Arrays.asList("1")));
        result.put("2", new ArrayList<>(Arrays.asList("2")));
        result.put("3", new ArrayList<String>());
        Iterator<String> iterator = new BulkResultIterator<>(keys, result);
        assertTrue(iterator.hasNext());
        assertEquals("1", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("2", iterator.next());
        assertTrue(iterator.hasNext());
        assertNull(iterator.next());
        assertFalse(iterator.hasNext());
    }

}
