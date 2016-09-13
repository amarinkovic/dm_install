package pro.documentum.util.java;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Arrays {

    private static final Map<Class<?>, Class<?>> ARRAY_CLASSES;

    static {
        ARRAY_CLASSES = new ConcurrentHashMap<>();
        putArray(boolean.class);
        putArray(int.class);
        putArray(double.class);
        putArray(short.class);
        putArray(float.class);
        putArray(long.class);
        putArray(byte.class);
        putArray(Boolean.class);
        putArray(Integer.class);
        putArray(Double.class);
        putArray(Short.class);
        putArray(Float.class);
        putArray(Long.class);
        putArray(Byte.class);
        putArray(String.class);
        putArray(Date.class);
        putArray(Calendar.class);
        putArray(GregorianCalendar.class);
    }

    private Arrays() {
        super();
    }

    static Class<?> putArray(final Class<?> cls) {
        Class<?> arrayClass = Array.newInstance(cls, 0).getClass();
        ARRAY_CLASSES.put(cls, arrayClass);
        return arrayClass;
    }

    public static Class<?> getArrayClass(final String name) {
        return getArrayClass(Classes.getClass(name));
    }

    public static Class<?> getArrayClass(final Class<?> cls) {
        Class<?> result = ARRAY_CLASSES.get(cls);
        if (result != null) {
            return result;
        }
        return putArray(cls);
    }

}
