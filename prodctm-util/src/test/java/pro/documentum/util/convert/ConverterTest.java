package pro.documentum.util.convert;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfValue;

import pro.documentum.junit.DfcTestSupport;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ConverterTest extends DfcTestSupport {

    private Converter _converter;

    @Override
    protected void doPostSetup() throws Exception {
        _converter = new Converter();
    }

    @Test
    public void testString1() throws Exception {
        assertEquals("", _converter.convert(null, IDfValue.DF_STRING));
        assertEquals("X", _converter.convert("X", IDfValue.DF_STRING));
        assertEquals("X", _converter.convert(new DfValue("X"),
                IDfValue.DF_STRING));
        IDfValue value = mock(IDfValue.class);
        when(value.asString()).thenReturn("X");
        assertEquals("X", _converter.convert(value, IDfValue.DF_STRING));
    }

}
