package pro.documentum.util.java;

import java.lang.reflect.Modifier;

import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Exceptions {

    private Exceptions() {
        super();
    }

    public static boolean inStack(final Throwable t,
            final Class<? extends Throwable> clazz) {
        Throwable local = t;

        if (!Modifier.isFinal(clazz.getModifiers())) {
            Logger.warn("Class " + clazz + " is not final");
        }

        do {
            if (clazz.isAssignableFrom(local.getClass())) {
                return true;
            }
            local = local.getCause();
        } while (local != null);

        return false;
    }

}
