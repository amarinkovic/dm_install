package pro.documentum.util.convert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.common.IDfValue;

import pro.documentum.util.convert.impl.BooleanConverter;
import pro.documentum.util.convert.impl.DoubleConverter;
import pro.documentum.util.convert.impl.IDfIdConverter;
import pro.documentum.util.convert.impl.IDfTimeConverter;
import pro.documentum.util.convert.impl.IntegerConverter;
import pro.documentum.util.convert.impl.StringConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Converter {

    private static final Map<Integer, IConverter> CONVERTERS = new HashMap<Integer, IConverter>();

    static {
        CONVERTERS.put(IDfValue.DF_BOOLEAN, new BooleanConverter());
        CONVERTERS.put(IDfValue.DF_INTEGER, new IntegerConverter());
        CONVERTERS.put(IDfValue.DF_DOUBLE, new DoubleConverter());
        CONVERTERS.put(IDfValue.DF_STRING, new StringConverter());
        CONVERTERS.put(IDfValue.DF_TIME, new IDfTimeConverter());
        CONVERTERS.put(IDfValue.DF_ID, new IDfIdConverter());
    }

    private static final Converter INSTANCE = new Converter();

    private Converter() {
        super();
    }

    public static Converter getInstance() {
        return INSTANCE;
    }

    private static IConverter getConverter(final int type)
        throws ParseException {
        IConverter converter = CONVERTERS.get(type);
        if (converter == null) {
            throw new ParseException("Unknown datatype", type);
        }
        return CONVERTERS.get(type);
    }

    @SuppressWarnings("unchecked")
    private <T> T convert(final IConverter converter, final Object value)
        throws ParseException {
        return (T) converter.convert(value);
    }

    public <T> T convert(final Object value, final int type)
        throws ParseException {
        if (value == null) {
            return null;
        }
        if (value instanceof Collection) {
            return convert((Collection) value, type);
        }
        return convert(getConverter(type), value);
    }

    @SuppressWarnings("unchecked")
    private <T> T convert(final Collection values, final int type)
        throws ParseException {
        IConverter converter = getConverter(type);
        Collection result = newCollection(values.getClass());
        for (Object value : values) {
            result.add(convert(converter, value));
        }
        return (T) result;
    }

    @SuppressWarnings("unchecked")
    private Collection newCollection(
            final Class<? extends Collection> collectionCass) {
        // noinspection TryWithIdenticalCatches
        try {
            Constructor<? extends Collection> ctor = getDefaultCtor(collectionCass);
            if (ctor != null) {
                return ctor.newInstance();
            }
            if (List.class.isAssignableFrom(collectionCass)) {
                return new ArrayList();
            }
            if (Set.class.isAssignableFrom(collectionCass)) {
                return new HashSet();
            }
            throw new IllegalArgumentException(
                    "Unable to create collection with class: " + collectionCass);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Constructor<T> getDefaultCtor(final Class<T> cls) {
        try {
            return cls.getConstructor();
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

}
