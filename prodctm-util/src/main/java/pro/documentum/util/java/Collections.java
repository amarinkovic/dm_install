package pro.documentum.util.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Collections {

    private static final Map<Class, Constructor> COLLECTION_CLASSES;

    static {
        COLLECTION_CLASSES = new ConcurrentHashMap<>();
        COLLECTION_CLASSES.put(List.class, putCollection(ArrayList.class));
        COLLECTION_CLASSES.put(Set.class, putCollection(HashSet.class));
        putCollection(LinkedList.class);
        putCollection(LinkedHashSet.class);
        putCollection(TreeSet.class);
    }

    private Collections() {
        super();
    }

    private static Constructor putCollection(final Class<?> cls) {
        Constructor ctor = getDefaultCtor(cls);
        if (ctor == null) {
            if (List.class.isAssignableFrom(cls)) {
                ctor = COLLECTION_CLASSES.get(List.class);
            }
            if (Set.class.isAssignableFrom(cls)) {
                ctor = COLLECTION_CLASSES.get(Set.class);
            }
        }
        if (ctor == null) {
            throw new IllegalArgumentException(
                    "Unable to create collection with class: " + cls);
        }
        COLLECTION_CLASSES.put(cls, ctor);
        return ctor;
    }

    @SuppressWarnings("unchecked")
    public static <T, C extends Collection<?>> Collection<T> newCollection(
            final Class<C> collectionCass) {
        // noinspection TryWithIdenticalCatches
        try {
            Constructor<C> ctor = COLLECTION_CLASSES.get(collectionCass);
            if (ctor == null) {
                ctor = putCollection(collectionCass);
            }
            return (Collection<T>) ctor.newInstance();
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
