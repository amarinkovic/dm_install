package pro.documentum.util.convert.impl;

import java.util.Map;

import pro.documentum.util.convert.IConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractConverter<F, T> implements IConverter<F, T> {

    protected abstract Map<Class, IConverter<?, T>> getConverters();

    protected final IConverter<?, T> getConverter(final Object value) {
        if (value == null) {
            return null;
        }
        Map<Class, IConverter<?, T>> converters = getConverters();
        Class cls = value.getClass();
        while (cls != Object.class) {
            IConverter<?, T> converter = converters.get(cls);
            if (converter != null) {
                return converter;
            }
            cls = cls.getSuperclass();
        }
        for (Class iface : value.getClass().getInterfaces()) {
            IConverter<?, T> converter = converters.get(iface);
            if (converter != null) {
                return converter;
            }
        }
        return null;
    }

}
