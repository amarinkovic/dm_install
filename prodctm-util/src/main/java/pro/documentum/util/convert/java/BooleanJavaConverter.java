package pro.documentum.util.convert.java;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import com.documentum.fc.common.IDfValue;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BooleanJavaConverter extends AbstractJavaConverter<Boolean> {

    public BooleanJavaConverter() {
        super();
    }

    @Override
    protected Boolean defaultValue() {
        return false;
    }

    @Override
    public List<Class<Boolean>> getJavaType() {
        return Arrays.asList(boolean.class, Boolean.class);
    }

    @Override
    protected Boolean doConvert(final IDfValue value) throws ParseException {
        return value.asBoolean();
    }

}
