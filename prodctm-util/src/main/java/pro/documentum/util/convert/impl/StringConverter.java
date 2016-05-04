package pro.documentum.util.convert.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfValue;

import pro.documentum.util.convert.IConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class StringConverter extends AbstractConverter<Object, String> {

    public static final String EMPTY = "";

    private static final Map<Class, IConverter<?, String>> CONVERTERS = new HashMap<Class, IConverter<?, String>>();

    static {
        CONVERTERS.put(String.class, new StringToString());
        CONVERTERS.put(IDfValue.class, new IDfValueToString());
        CONVERTERS.put(DfValue.class, CONVERTERS.get(IDfValue.class));
    }

    public StringConverter() {
        super();
    }

    public static String defaultValue() {
        return EMPTY;
    }

    @Override
    protected Map<Class, IConverter<?, String>> getConverters() {
        return CONVERTERS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String convert(final Object value) throws ParseException {
        if (value == null) {
            return defaultValue();
        }
        IConverter converter = getConverter(value);
        if (converter == null) {
            throw new ParseException("Unable to convert " + value
                    + " to string", 0);
        }
        return (String) converter.convert(value);
    }

    static class StringToString implements IConverter<String, String> {

        StringToString() {
            super();
        }

        @Override
        public String convert(final String obj) {
            return obj;
        }

    }

    static class IDfValueToString implements IConverter<IDfValue, String> {

        IDfValueToString() {
            super();
        }

        @Override
        public String convert(final IDfValue value) {
            return value.asString();
        }

    }

}
