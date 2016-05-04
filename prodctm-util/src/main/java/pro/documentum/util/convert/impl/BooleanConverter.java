package pro.documentum.util.convert.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfValue;

import pro.documentum.util.convert.IConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BooleanConverter extends AbstractConverter<Object, Boolean> {

    private static final Set<String> TRUE_VALUES = new HashSet<String>();

    static {
        TRUE_VALUES.add("t");
        TRUE_VALUES.add("T");
        TRUE_VALUES.add("true");
        TRUE_VALUES.add("TRUE");
        TRUE_VALUES.add("1");
        TRUE_VALUES.add("1.0");
    }

    private static final Map<Class, IConverter<?, Boolean>> CONVERTERS = new HashMap<Class, IConverter<?, Boolean>>();

    static {
        CONVERTERS.put(String.class, new StringToBoolean());
        CONVERTERS.put(IDfValue.class, new IDfValueToBoolean());
        CONVERTERS.put(DfValue.class, CONVERTERS.get(IDfValue.class));
        CONVERTERS.put(Boolean.class, new BooleanToBoolean());
        CONVERTERS.put(boolean.class, CONVERTERS.get(Boolean.class));
    }

    public BooleanConverter() {
        super();
    }

    public static Boolean defaultValue() {
        return Boolean.FALSE;
    }

    @Override
    protected Map<Class, IConverter<?, Boolean>> getConverters() {
        return CONVERTERS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Boolean convert(final Object value) throws ParseException {
        if (value == null) {
            return defaultValue();
        }
        IConverter converter = getConverter(value);
        if (converter == null) {
            throw new ParseException("Unable to convert " + value
                    + " to boolean", 0);
        }
        return (Boolean) converter.convert(value);
    }

    static class BooleanToBoolean implements IConverter<Boolean, Boolean> {

        BooleanToBoolean() {
            super();
        }

        @Override
        public Boolean convert(final Boolean obj) {
            return obj;
        }

    }

    static class IDfValueToBoolean implements IConverter<IDfValue, Boolean> {

        IDfValueToBoolean() {
            super();
        }

        @Override
        public Boolean convert(final IDfValue value) {
            return value.asBoolean();
        }

    }

    static class StringToBoolean implements IConverter<String, Boolean> {

        StringToBoolean() {
            super();
        }

        @Override
        public Boolean convert(final String value) {
            return TRUE_VALUES.contains(value);
        }

    }

}
