package pro.documentum.util.convert;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfValue;

import pro.documentum.util.convert.datastore.BooleanDataStoreConverter;
import pro.documentum.util.convert.datastore.DoubleDataStoreConverter;
import pro.documentum.util.convert.datastore.IDataStoreConverter;
import pro.documentum.util.convert.datastore.IDfIdDataStoreConverter;
import pro.documentum.util.convert.datastore.IDfTimeDataStoreConverter;
import pro.documentum.util.convert.datastore.IntegerDataStoreConverter;
import pro.documentum.util.convert.datastore.StringDataStoreConverter;
import pro.documentum.util.convert.java.BooleanJavaConverter;
import pro.documentum.util.convert.java.CalendarJavaConverter;
import pro.documentum.util.convert.java.DateJavaConverter;
import pro.documentum.util.convert.java.DoubleJavaConverter;
import pro.documentum.util.convert.java.IJavaConverter;
import pro.documentum.util.convert.java.IntegerJavaConverter;
import pro.documentum.util.convert.java.StringJavaConverter;
import pro.documentum.util.java.Classes;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Converter {

    private static final Map<Integer, IConverter<Object, ?>> DATASTORE;
    private static final Map<Class<?>, IConverter<IDfValue, ?>> JAVA;
    private static final Converter INSTANCE = new Converter();

    static {
        DATASTORE = new HashMap<>();
        putDataStore(new BooleanDataStoreConverter<>());
        putDataStore(new IntegerDataStoreConverter<>());
        putDataStore(new DoubleDataStoreConverter<>());
        putDataStore(new StringDataStoreConverter<>());
        putDataStore(new IDfTimeDataStoreConverter<>());
        putDataStore(new IDfIdDataStoreConverter<>());
    }

    static {
        JAVA = new HashMap<>();
        putJava(new BooleanJavaConverter());
        putJava(new CalendarJavaConverter());
        putJava(new DateJavaConverter());
        putJava(new DoubleJavaConverter());
        putJava(new IntegerJavaConverter());
        putJava(new StringJavaConverter());
    }

    private Converter() {
        super();
    }

    private static void putDataStore(
            final IDataStoreConverter<Object, ?> converter) {
        DATASTORE.put(converter.getDataStoreType(), converter);
    }

    private static void putJava(final IJavaConverter<IDfValue, ?> converter) {
        for (Class<?> cls : converter.getJavaType()) {
            JAVA.put(cls, converter);
        }
    }

    public static Converter getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private static <T> IConverter<Object, T> getConverter(final int type)
        throws ParseException {
        IConverter<Object, ?> converter = DATASTORE.get(type);
        if (converter == null) {
            throw new ParseException("Unknown datatype " + type, 0);
        }
        return (IConverter<Object, T>) converter;
    }

    @SuppressWarnings("unchecked")
    private static <T> IConverter<IDfValue, T> getConverter(final Class<?> type)
        throws ParseException {
        IConverter<IDfValue, ?> converter = JAVA.get(type);
        if (converter == null) {
            throw new ParseException("Unknown datatype " + type, 0);
        }
        return (IConverter<IDfValue, T>) converter;
    }

    @SuppressWarnings("unchecked")
    public <T> T toDataStore(final Object value, final int type)
        throws ParseException {
        if (value == null) {
            return null;
        }
        Class<?> cls = value.getClass();
        if (Collection.class.isAssignableFrom(cls)) {
            return (T) collection2DataStore((Collection) value, type);
        }
        if (cls.isArray()) {
            return (T) array2DataStore(value, type);
        }
        return Converter.<T> getConverter(type).convert(value);
    }

    public <T> T fromDataStore(final IDfTypedObject object,
            final String attrName, final Class<T> targetClass, final int index)
        throws DfException {
        try {
            IConverter<IDfValue, T> converter = getConverter(targetClass);
            IDfValue value = object.getRepeatingValue(attrName, index);
            return converter.convert(value);
        } catch (ParseException ex) {
            throw new DfException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Collection<T> collection2DataStore(final Collection<?> values,
            final int type) throws ParseException {
        IConverter<Object, T> converter = getConverter(type);
        Collection<T> result = Classes
                .newCollection((Class<? extends Collection<?>>) values
                        .getClass());
        for (Object value : values) {
            result.add(converter.convert(value));
        }
        return result;
    }

    private <T> List<T> array2DataStore(final Object values, final int type)
        throws ParseException {
        IConverter<Object, T> converter = getConverter(type);
        List<T> result = new ArrayList<>();
        for (int i = 0, n = Array.getLength(values); i < n; i++) {
            result.add(converter.convert(Array.get(values, i)));
        }
        return result;
    }

}
