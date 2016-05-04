package pro.documentum.util.convert.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfValue;

import pro.documentum.util.convert.IConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class IntegerConverter extends AbstractConverter<Object, Integer> {

    private static final Map<Class, IConverter<?, Integer>> CONVERTERS = new HashMap<Class, IConverter<?, Integer>>();

    static {
        CONVERTERS.put(String.class, new StringToInteger());
        CONVERTERS.put(IDfValue.class, new IDfValueToInteger());
        CONVERTERS.put(DfValue.class, CONVERTERS.get(IDfValue.class));
        CONVERTERS.put(Integer.class, new IntegerToInteger());
        CONVERTERS.put(int.class, CONVERTERS.get(Integer.class));
    }

    public IntegerConverter() {
        super();
    }

    public static Integer defaultValue() {
        return 0;
    }

    @Override
    protected Map<Class, IConverter<?, Integer>> getConverters() {
        return CONVERTERS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Integer convert(final Object value) throws ParseException {
        if (value == null) {
            return defaultValue();
        }
        IConverter converter = getConverter(value);
        if (converter == null) {
            throw new ParseException("Unable to convert " + value
                    + " to integer", 0);
        }
        return (Integer) converter.convert(value);
    }

    static class IntegerToInteger implements IConverter<Integer, Integer> {

        IntegerToInteger() {
            super();
        }

        @Override
        public Integer convert(final Integer obj) {
            return obj;
        }

    }

    static class IDfValueToInteger implements IConverter<IDfValue, Integer> {

        IDfValueToInteger() {
            super();
        }

        @Override
        public Integer convert(final IDfValue value) {
            return value.asInteger();
        }

    }

    static class StringToInteger implements IConverter<String, Integer> {

        StringToInteger() {
            super();
        }

        @Override
        public Integer convert(final String value) {
            if (StringUtils.isBlank(value)) {
                return defaultValue();
            }
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException ex) {
                return defaultValue();
            }
        }

    }

}
