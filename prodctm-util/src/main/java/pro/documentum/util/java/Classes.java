package pro.documentum.util.java;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Classes {

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

    private Classes() {
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
            throw new RuntimeException(ex);
        }
    }

    public static Class<?> getArrayClass(final String name) {
        return getArrayClass(getClass(name));
    }

    public static Class<?> getArrayClass(final Class<?> cls) {
        return Array.newInstance(cls).getClass();
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> newCollection(
            final Class<? extends Collection<?>> collectionCass) {
        // noinspection TryWithIdenticalCatches
        try {
            Constructor<? extends Collection<?>> ctor = getDefaultCtor(collectionCass);
            if (ctor != null) {
                return (Collection<T>) ctor.newInstance();
            }
            if (List.class.isAssignableFrom(collectionCass)) {
                return new ArrayList<>();
            }
            if (Set.class.isAssignableFrom(collectionCass)) {
                return new HashSet<>();
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

    private static <T> Constructor<T> getDefaultCtor(final Class<T> cls) {
        try {
            return cls.getConstructor();
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

}
