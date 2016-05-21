package pro.documentum.util.convert.java;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import com.documentum.fc.common.IDfValue;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class IntegerJavaConverter extends AbstractJavaConverter<Integer> {

    public IntegerJavaConverter() {
        super();
    }

    @Override
    public List<Class<Integer>> getJavaType() {
        return Arrays.asList(int.class, Integer.class);
    }

    @Override
    protected Integer doConvert(final IDfValue value) throws ParseException {
        return value.asInteger();
    }

}
