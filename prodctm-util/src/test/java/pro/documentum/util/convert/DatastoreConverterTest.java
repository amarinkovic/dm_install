package pro.documentum.util.convert;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfValue;

import pro.documentum.junit.DfcTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@SuppressWarnings({"unchecked", "rawtypes" })
public class DatastoreConverterTest extends DfcTestSupport {

    private Converter _converter;

    @Override
    protected void doPostSetup() throws Exception {
        _converter = Converter.getInstance();
    }

    @Test
    public void testString() throws Exception {
        assertEquals(null, _converter.toDataStore(null, IDfValue.DF_STRING));
        assertEquals("X", _converter.toDataStore("X", IDfValue.DF_STRING));
        assertEquals("X",
                _converter.toDataStore(new DfValue("X"), IDfValue.DF_STRING));
        IDfValue value = mock(IDfValue.class);
        when(value.asString()).thenReturn("X");
        assertEquals("X", _converter.toDataStore(value, IDfValue.DF_STRING));
    }

    @Test
    public void testStringArray() throws Exception {
        assertEquals(null, _converter.toDataStore(null, IDfValue.DF_STRING));
        assertEquals(Arrays.asList("X", "x", "1", "10", "1.1", "2147483647",
                "-2147483648", null), _converter.toDataStore(new Object[] {"X",
            'x', 1, Integer.valueOf(10), 1.1, Integer.MAX_VALUE,
            Integer.MIN_VALUE, null }, IDfValue.DF_STRING));
    }

    @Test
    public void testStringList() throws Exception {
        assertEquals(null, _converter.toDataStore(null, IDfValue.DF_STRING));
        List testList = new ArrayList();
        testList.add("X");
        testList.add('x');
        testList.add(1);
        testList.add(Integer.valueOf(10));
        testList.add(1.1);
        testList.add(Integer.MAX_VALUE);
        testList.add(Integer.MIN_VALUE);
        testList.add(null);
        assertEquals(Arrays.asList("X", "x", "1", "10", "1.1", "2147483647",
                "-2147483648", null), _converter.toDataStore(testList,
                IDfValue.DF_STRING));
    }

    @Test
    public void testInteger() throws Exception {
        assertEquals(null, _converter.toDataStore(null, IDfValue.DF_INTEGER));
        assertEquals(1, _converter.toDataStore("1", IDfValue.DF_INTEGER));
        assertEquals(1,
                _converter.toDataStore(new DfValue("1"), IDfValue.DF_INTEGER));
        IDfValue value = mock(IDfValue.class);
        when(value.asInteger()).thenReturn(1);
        assertEquals(1, _converter.toDataStore(value, IDfValue.DF_INTEGER));
    }

}
