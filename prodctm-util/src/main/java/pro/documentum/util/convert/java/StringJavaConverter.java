package pro.documentum.util.convert.java;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import com.documentum.fc.common.IDfValue;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class StringJavaConverter extends AbstractJavaConverter<String> {

    public StringJavaConverter() {
        super();
    }

    @Override
    public List<Class<String>> getJavaType() {
        return Collections.singletonList(String.class);
    }

    @Override
    protected String doConvert(final IDfValue value) throws ParseException {
        return value.asString();
    }

}
