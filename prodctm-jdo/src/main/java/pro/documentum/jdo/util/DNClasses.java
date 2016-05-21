package pro.documentum.jdo.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.datanucleus.exceptions.NucleusException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNClasses {

    private static final Map<String, Class<?>> BUILTIN_CLASSES;

    static {
        BUILTIN_CLASSES = new HashMap<>();
        putBuiltIn(boolean.class);
        putBuiltIn(int.class);
        putBuiltIn(double.class);
        putBuiltIn(short.class);
        putBuiltIn(float.class);
        putBuiltIn(long.class);
        putBuiltIn(Boolean.class);
        putBuiltIn(Integer.class);
        putBuiltIn(Double.class);
        putBuiltIn(Short.class);
        putBuiltIn(Float.class);
        putBuiltIn(Long.class);
        putBuiltIn(String.class);
        putBuiltIn(Date.class);
        putBuiltIn(Calendar.class);
        putBuiltIn(GregorianCalendar.class);
    }

    private DNClasses() {
        super();
    }

    private static void putBuiltIn(final Class<?> cls) {
        BUILTIN_CLASSES.put(cls.getName(), cls);
    }

    public static Class<?> getClass(final String name) {
        try {
            Class<?> result = BUILTIN_CLASSES.get(name);
            if (result != null) {
                return result;
            }
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new NucleusException(ex.getMessage(), ex);
        }
    }

}
