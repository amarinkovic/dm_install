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
public class DoubleConverter extends AbstractConverter<Object, Double> {

    private static final Map<Class, IConverter<?, Double>> CONVERTERS = new HashMap<Class, IConverter<?, Double>>();

    static {
        CONVERTERS.put(String.class, new StringToDouble());
        CONVERTERS.put(IDfValue.class, new IDfValueToDouble());
        CONVERTERS.put(DfValue.class, CONVERTERS.get(IDfValue.class));
        CONVERTERS.put(Double.class, new DoubleToDouble());
        CONVERTERS.put(double.class, CONVERTERS.get(Double.class));
    }

    public DoubleConverter() {
        super();
    }

    public static Double defaultValue() {
        return 0D;
    }

    @Override
    protected Map<Class, IConverter<?, Double>> getConverters() {
        return CONVERTERS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Double convert(final Object value) throws ParseException {
        if (value == null) {
            return defaultValue();
        }
        IConverter converter = getConverter(value);
        if (converter == null) {
            throw new ParseException("Unable to convert " + value
                    + " to double", 0);
        }
        return (Double) converter.convert(value);
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
