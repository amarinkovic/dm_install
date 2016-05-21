package pro.documentum.util.convert.java;

import java.text.ParseException;

import com.documentum.fc.common.IDfValue;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractJavaConverter<T> implements
        IJavaConverter<IDfValue, T> {

    @Override
    public T convert(final IDfValue value) throws ParseException {
        if (value == null) {
            return defaultValue();
        }
        return doConvert(value);
    }

    protected abstract T doConvert(final IDfValue value) throws ParseException;

    protected T defaultValue() {
        return null;
    }

}
