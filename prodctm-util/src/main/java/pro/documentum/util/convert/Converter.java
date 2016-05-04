package pro.documentum.util.convert;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfValue;

import pro.documentum.util.convert.impl.BooleanConverter;
import pro.documentum.util.convert.impl.DoubleConverter;
import pro.documentum.util.convert.impl.IDfTimeConverter;
import pro.documentum.util.convert.impl.IntegerConverter;
import pro.documentum.util.convert.impl.StringConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Converter {

    private static final Map<Integer, IConverter> CONVERTERS = new HashMap<Integer, IConverter>();

    static {
        CONVERTERS.put(IDfValue.DF_BOOLEAN, new BooleanConverter());
        CONVERTERS.put(IDfValue.DF_INTEGER, new IntegerConverter());
        CONVERTERS.put(IDfValue.DF_DOUBLE, new DoubleConverter());
        CONVERTERS.put(IDfValue.DF_STRING, new StringConverter());
        CONVERTERS.put(IDfValue.DF_TIME, new IDfTimeConverter());
    }

    public Converter() {
        super();
    }

    @SuppressWarnings("unchecked")
    public <T> T convert(final Object value, final int type)
        throws ParseException {
        IConverter converter = CONVERTERS.get(type);
        if (converter == null) {
            throw new ParseException("Unknown datatype", type);
        }
        return (T) converter.convert(value);
    }

    public IDfId convertToIDfId(final IDfValue value) {
        return Objects.requireNonNull(value).asId();
    }

    public IDfId convertToIDfId(final String value) {
        if (StringUtils.isBlank(value)) {
            return DfId.DF_NULLID;
        }
        if (!DfId.isObjectId(value)) {
            return DfId.DF_NULLID;
        }
        return new DfId(value);
    }

    public IDfId convertToIDfId(final Object value) {
        if (value == null) {
            return DfId.DF_NULLID;
        }
        if (value instanceof IDfValue) {
            return convertToIDfId((IDfValue) value);
        } else if (value instanceof String) {
            return convertToIDfId((String) value);
        } else if (value instanceof IDfId) {
            return (IDfId) value;
        }
        throw new IllegalArgumentException("Unable to convert \"" + value
                + "\" to double");
    }

}
