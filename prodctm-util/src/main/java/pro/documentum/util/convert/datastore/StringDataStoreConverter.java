package pro.documentum.util.convert.datastore;

import java.util.HashMap;
import java.util.Map;

import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfValue;

import pro.documentum.util.convert.IConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class StringDataStoreConverter<F> extends
        AbstractDataStoreConverter<F, String> {

    private static final Map<Class<?>, IConverter<?, String>> CONVERTERS;

    static {
        CONVERTERS = new HashMap<>();
        CONVERTERS.put(String.class, new StringToString());
        CONVERTERS.put(IDfValue.class, new IDfValueToString());
        CONVERTERS.put(DfValue.class, CONVERTERS.get(IDfValue.class));
        CONVERTERS.put(Number.class, new NumberToString());
        for (Class<?> cls : new Class<?>[] {int.class, double.class,
            long.class, float.class, short.class, }) {
            CONVERTERS.put(cls, CONVERTERS.get(Number.class));
        }
        CONVERTERS.put(Character.class, new CharacterToString());
        CONVERTERS.put(char.class, CONVERTERS.get(Character.class));
    }

    public StringDataStoreConverter() {
        super();
    }

    @Override
    public int getDataStoreType() {
        return IDfValue.DF_STRING;
    }

    @Override
    protected Map<Class<?>, IConverter<?, String>> getConverters() {
        return CONVERTERS;
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

    static class NumberToString implements IConverter<Number, String> {

        NumberToString() {
            super();
        }

        @Override
        public String convert(final Number obj) {
            return String.valueOf(obj);
        }

    }

    static class CharacterToString implements IConverter<Character, String> {

        CharacterToString() {
            super();
        }

        @Override
        public String convert(final Character obj) {
            return "" + obj;
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
