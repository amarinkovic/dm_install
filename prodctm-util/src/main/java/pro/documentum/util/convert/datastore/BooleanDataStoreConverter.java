package pro.documentum.util.convert.datastore;

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
public class BooleanDataStoreConverter<F> extends
        AbstractDataStoreConverter<F, Boolean> {

    private static final Set<String> TRUE_VALUES = new HashSet<>();
    private static final Map<Class<?>, IConverter<?, Boolean>> CONVERTERS;

    static {
        TRUE_VALUES.add("t");
        TRUE_VALUES.add("T");
        TRUE_VALUES.add("true");
        TRUE_VALUES.add("TRUE");
        TRUE_VALUES.add("1");
        TRUE_VALUES.add("1.0");
    }

    static {
        CONVERTERS = new HashMap<>();
        CONVERTERS.put(String.class, new StringToBoolean());
        CONVERTERS.put(IDfValue.class, new IDfValueToBoolean());
        CONVERTERS.put(DfValue.class, CONVERTERS.get(IDfValue.class));
        CONVERTERS.put(Boolean.class, new BooleanToBoolean());
        CONVERTERS.put(boolean.class, CONVERTERS.get(Boolean.class));
        CONVERTERS.put(Integer.class, new IntegerToBoolean());
        CONVERTERS.put(int.class, CONVERTERS.get(Integer.class));
    }

    public BooleanDataStoreConverter() {
        super();
    }

    @Override
    public int getDataStoreType() {
        return IDfValue.DF_BOOLEAN;
    }

    @Override
    protected Map<Class<?>, IConverter<?, Boolean>> getConverters() {
        return CONVERTERS;
    }

    @Override
    protected Boolean doConvert(final F value) throws ParseException {
        IConverter<F, Boolean> converter = getConverter(value);
        if (converter == null) {
            throw new ParseException("Unable to convert " + value
                    + " to boolean", 0);
        }
        return converter.convert(value);
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

    static class IntegerToBoolean implements IConverter<Integer, Boolean> {

        IntegerToBoolean() {
            super();
        }

        @Override
        public Boolean convert(final Integer value) {
            return value == 1;
        }

    }

}
