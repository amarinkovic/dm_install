package pro.documentum.util.java;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Classes {

    private static final Map<String, Class<?>> BUILTIN_CLASSES;
    private static final Map<Class<?>, Class<?>> ARRAY_CLASSES;

    static {
        BUILTIN_CLASSES = new ConcurrentHashMap<>();
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

    static {
        ARRAY_CLASSES = new ConcurrentHashMap<>();
        for (Class<?> buildIn : BUILTIN_CLASSES.values()) {
            putArray(buildIn);
        }
    }

    private Classes() {
        super();
    }

    private static Class<?> putBuiltIn(final Class<?> cls) {
        BUILTIN_CLASSES.put(cls.getName(), cls);
        return cls;
    }

    private static Class<?> putArray(final Class<?> cls) {
        Class<?> arrayClass = Array.newInstance(cls, 0).getClass();
        ARRAY_CLASSES.put(cls, arrayClass);
        return arrayClass;
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

    public static Class<?> getArrayClass(final String name) {
        return getArrayClass(getClass(name));
    }

    public static Class<?> getArrayClass(final Class<?> cls) {
        Class<?> result = ARRAY_CLASSES.get(cls);
        if (result != null) {
            return result;
        }
        return putArray(cls);
    }

    @SuppressWarnings("unchecked")
    public static <T, C extends Collection<?>> Collection<T> newCollection(
            final Class<C> collectionCass) {
        // noinspection TryWithIdenticalCatches
        try {
            Constructor<C> ctor = getDefaultCtor(collectionCass);
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
