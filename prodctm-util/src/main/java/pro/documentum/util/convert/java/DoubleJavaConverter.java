package pro.documentum.util.convert.java;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import com.documentum.fc.common.IDfValue;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DoubleJavaConverter extends AbstractJavaConverter<Double> {

    public DoubleJavaConverter() {
        super();
    }

    @Override
    public List<Class<Double>> getJavaType() {
        return Arrays.asList(double.class, Double.class);
    }

    @Override
    protected Double doConvert(final IDfValue value) throws ParseException {
        if (value == null) {
            return defaultValue();
        }
        return value.asDouble();
    }

}
