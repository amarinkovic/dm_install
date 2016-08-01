package pro.documentum.util.convert.datastore;

import java.text.ParseException;
import java.util.Map;

import pro.documentum.util.convert.IConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
abstract class AbstractDataStoreConverter<F, T> implements
        IDataStoreConverter<F, T> {

    protected abstract Map<Class<?>, IConverter<?, T>> getConverters();

    @Override
    public final T convert(final F obj) throws ParseException {
        if (obj == null) {
            return defaultValue();
        }
        return doConvert(obj);
    }

    protected T defaultValue() {
        return null;
    }

    protected T doConvert(final F obj) throws ParseException {
        return getConverter(obj).convert(obj);
    }

    @SuppressWarnings("unchecked")
    protected final IConverter<F, T> getConverter(final F value)
        throws ParseException {
        Map<Class<?>, IConverter<?, T>> converters = getConverters();
        Class<?> cls = value.getClass();
        while (cls != Object.class) {
            IConverter<?, T> converter = converters.get(cls);
            if (converter != null) {
                return (IConverter<F, T>) converter;
            }
            cls = cls.getSuperclass();
        }
        for (Class<?> iface : value.getClass().getInterfaces()) {
            IConverter<?, T> converter = converters.get(iface);
            if (converter != null) {
                return (IConverter<F, T>) converter;
            }
        }
        throw new ParseException("Unable to convert " + value + " of class "
                + value.getClass(), 0);
    }

}
