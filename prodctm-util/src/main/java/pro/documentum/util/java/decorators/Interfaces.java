package pro.documentum.util.java.decorators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.ClassUtils;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Interfaces {

    private static final ConcurrentMap<Class<?>, List<Class<?>>> CACHE = new ConcurrentHashMap<>();

    private Interfaces() {
        super();
    }

    @SuppressWarnings("unchecked")
    public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        List<Class<?>> result = CACHE.get(cls);
        if (result == null) {
            result = ClassUtils.getAllInterfaces(cls);
            CACHE.putIfAbsent(cls, result);
        }
        return new ArrayList<>(result);
    }

}
