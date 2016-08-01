package pro.documentum.util.convert.datastore;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfValue;

import pro.documentum.util.convert.IConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class IntegerDataStoreConverter<F> extends
        AbstractDataStoreConverter<F, Integer> {

    private static final Map<Class<?>, IConverter<?, Integer>> CONVERTERS;

    static {
        CONVERTERS = new HashMap<>();
        CONVERTERS.put(String.class, new StringToInteger());
        CONVERTERS.put(IDfValue.class, new IDfValueToInteger());
        CONVERTERS.put(DfValue.class, CONVERTERS.get(IDfValue.class));
        CONVERTERS.put(Integer.class, new IntegerToInteger());
        CONVERTERS.put(int.class, CONVERTERS.get(Integer.class));
    }

    public IntegerDataStoreConverter() {
        super();
    }

    @Override
    public int getDataStoreType() {
        return IDfValue.DF_INTEGER;
    }

    @Override
    protected Map<Class<?>, IConverter<?, Integer>> getConverters() {
        return CONVERTERS;
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

        static Integer defaultValue() {
            return 0;
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
