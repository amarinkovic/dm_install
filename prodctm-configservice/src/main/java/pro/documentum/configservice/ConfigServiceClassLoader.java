package pro.documentum.configservice;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
final class ConfigServiceClassLoader extends ClassLoader {

    ConfigServiceClassLoader(final ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> loadClass(final String className, final boolean resolve)
        throws ClassNotFoundException {
        if (!needOverride(className)) {
            return super.loadClass(className, resolve);
        }
        Class<?> result = load(className);
        if (result == null) {
            return super.loadClass(className, resolve);
        }
        if (resolve) {
            resolveClass(result);
        }
        return result;
    }

    protected boolean needOverride(final String className) {
        return className.startsWith("com.documentum.services.config.impl");
    }

    private Class<?> load(final String className) throws ClassNotFoundException {
        try {
            Class<?> result = findLoadedClass(className);
            if (result != null) {
                return result;
            }
            InputStream inputStream = openStream(className);
            if (inputStream == null) {
                return null;
            }
            byte[] bytes = IOUtils.toByteArray(inputStream);
            result = defineClass(className, bytes, 0, bytes.length);
            return result;
        } catch (IOException ex) {
            throw new ClassNotFoundException(className, ex);
        }
    }

    protected InputStream openStream(final String className) {
        String fileName = className.replace('.', '/') + ".class";
        return getParent().getResourceAsStream(fileName);
    }

}
