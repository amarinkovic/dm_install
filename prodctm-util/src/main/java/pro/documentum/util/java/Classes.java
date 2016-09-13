package pro.documentum.util.java;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Classes {

    private static final Map<String, Class<?>> BUILTIN_CLASSES;

    static {
        BUILTIN_CLASSES = new ConcurrentHashMap<>();
        putBuiltIn(boolean.class);
        putBuiltIn(int.class);
        putBuiltIn(double.class);
        putBuiltIn(short.class);
        putBuiltIn(float.class);
        putBuiltIn(long.class);
        putBuiltIn(byte.class);
        putBuiltIn(Boolean.class);
        putBuiltIn(Integer.class);
        putBuiltIn(Double.class);
        putBuiltIn(Short.class);
        putBuiltIn(Float.class);
        putBuiltIn(Long.class);
        putBuiltIn(Byte.class);
        putBuiltIn(String.class);
        putBuiltIn(Date.class);
        putBuiltIn(Calendar.class);
        putBuiltIn(GregorianCalendar.class);
    }

    private Classes() {
        super();
    }

    private static Class<?> putBuiltIn(final Class<?> cls) {
        BUILTIN_CLASSES.put(cls.getName(), cls);
        return cls;
    }

    public static Class<?> getClass(final String name) {
        try {
            Class<?> result = BUILTIN_CLASSES.get(name);
            if (result != null) {
                return result;
            }
            return putBuiltIn(Class.forName(name));
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

}
