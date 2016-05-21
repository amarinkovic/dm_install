package pro.documentum.util.convert.datastore;

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
public class DoubleDataStoreConverter<F> extends
        AbstractDataStoreConverter<F, Double> {

    private static final Map<Class<?>, IConverter<?, Double>> CONVERTERS;

    static {
        CONVERTERS = new HashMap<>();
        CONVERTERS.put(String.class, new StringToDouble());
        CONVERTERS.put(IDfValue.class, new IDfValueToDouble());
        CONVERTERS.put(DfValue.class, CONVERTERS.get(IDfValue.class));
        CONVERTERS.put(Double.class, new DoubleToDouble());
        CONVERTERS.put(double.class, CONVERTERS.get(Double.class));
    }

    public DoubleDataStoreConverter() {
        super();
    }

    @Override
    public int getDataStoreType() {
        return IDfValue.DF_DOUBLE;
    }

    @Override
    protected Map<Class<?>, IConverter<?, Double>> getConverters() {
        return CONVERTERS;
    }

    @Override
    protected Double doConvert(final F value) throws ParseException {
        IConverter<F, Double> converter = getConverter(value);
        if (converter == null) {
            throw new ParseException("Unable to convert " + value
                    + " to double", 0);
        }
        return converter.convert(value);
    }

    static class DoubleToDouble implements IConverter<Double, Double> {

        DoubleToDouble() {
            super();
        }

        @Override
        public Double convert(final Double obj) {
            return obj;
        }

    }

    static class IDfValueToDouble implements IConverter<IDfValue, Double> {

        IDfValueToDouble() {
            super();
        }

        @Override
        public Double convert(final IDfValue value) {
            return value.asDouble();
        }

    }

    static class StringToDouble implements IConverter<String, Double> {

        StringToDouble() {
            super();
        }

        static Double defaultValue() {
            return 0D;
        }

        @Override
        public Double convert(final String value) {
            if (StringUtils.isBlank(value)) {
                return defaultValue();
            }
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException ex) {
                return defaultValue();
            }
        }

    }

}
