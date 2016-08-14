package pro.documentum.util.preferences;

import java.io.File;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Level;

import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfPreferences;
import com.documentum.fc.common.impl.preferences.PreferencesManager;
import com.documentum.fc.common.impl.preferences.annotation.IntegerConstraint;
import com.documentum.fc.common.impl.preferences.annotation.Preference;

import pro.documentum.util.ISessionInvoker;
import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class PreferencesLoader {

    enum ParseState {
        NORMAL, ESCAPE, UNICODE_ESCAPE, SEEN_DOLLAR, IN_BRACKET
    }

    private static final String FILE_SEPARATOR = File.separator;

    private static final String PATH_SEPARATOR = File.pathSeparator;

    private static final String FILE_SEPARATOR_ALIAS = "/";

    private static final String PATH_SEPARATOR_ALIAS = ":";

    private PreferencesLoader() {
        super();
    }

    private static DfPreferences getPreferences() {
        return Log4jSilencer.invoke(
                new ISessionInvoker<DfPreferences, Void, RuntimeException>() {
                    @Override
                    public DfPreferences invoke(final Void session) {
                        return DfPreferences.getInstance();
                    }
                }, PreferencesManager.class);
    }

    public static List<String> getKnownPreferences() {
        List<String> result = new ArrayList<>();
        for (Field field : DfPreferences.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Preference.class)) {
                continue;
            }
            try {
                Object preference = field.get(null);
                if (!(preference instanceof String)) {
                    continue;
                }
                String preferenceName = (String) preference;
                result.add(preferenceName);
            } catch (IllegalAccessException e) {
                Logger.error(e);
            }
        }
        return result;
    }

    public static void load(final Map<String, String> persistentProperties) {
        load(persistentProperties, false);
    }

    public static void load(final Map<String, String> persistentProperties,
            final boolean initial) {
        Properties filtered = filterKnownProperties(persistentProperties);
        if (filtered == null || filtered.isEmpty()) {
            return;
        }
        getPreferences().loadProperties(filtered, initial);
    }

    private static Properties filterKnownProperties(
            final Map<String, String> properties) {
        Properties filtered = new Properties();
        for (Field field : DfPreferences.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Preference.class)) {
                continue;
            }
            filterKnownProperties(field, filtered, properties);
        }
        return filtered;
    }

    private static void filterKnownProperties(final Field field,
            final Properties output, final Map<String, String> input) {
        String preferenceName = null;
        try {
            Object preference = field.get(null);
            if (!(preference instanceof String)) {
                return;
            }
            preferenceName = (String) preference;
        } catch (IllegalAccessException e) {
            return;
        }

        if (!input.containsKey(preferenceName)) {
            return;
        }

        String preferenceValue = input.get(preferenceName);
        if (preferenceValue == null) {
            return;
        }

        Preference preference = field.getAnnotation(Preference.class);
        boolean repeating = preference.repeating();

        if (repeating) {
            filterRepeatingProperty(field, preferenceName, output, input);
        } else {
            filterSingleProperty(field, preferenceName, output, input);
        }

    }

    private static void filterRepeatingProperty(final Field field,
            final String preferenceName, final Properties output,
            final Map<String, String> input) {
        String preferenceValue = input.get(preferenceName);
        String[] values = preferenceValue.split(",");
        for (String value : values) {
            if (!isCorrectValue(field, value)) {
                return;
            }
        }
        output.put(preferenceName, values);
    }

    private static void filterSingleProperty(final Field field,
            final String preferenceName, final Properties output,
            final Map<String, String> input) {
        String preferenceValue = input.get(preferenceName);
        if (isCorrectValue(field, preferenceValue)) {
            output.put(preferenceName, preferenceValue);
        }
    }

    private static boolean isCorrectValue(final Field field,
            final String preferenceValue) {
        if (field.isAnnotationPresent(IntegerConstraint.class)) {
            try {
                // noinspection ResultOfMethodCallIgnored
                Integer.parseInt(preferenceValue);
                return true;
            } catch (NumberFormatException ex) {
                Logger.error(ex);
            }
            return false;
        }

        return true;
    }

    public static String replaceProperties(final String string) {
        return replaceProperties(string, null);
    }

    public static String replaceProperties(final String string,
            final Map<String, String> props) {
        char[] chars = string.toCharArray();
        StringBuilder buffer = new StringBuilder();
        boolean properties = false;
        ParseState state = ParseState.NORMAL;
        int start = 0;
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];

            if (c == '$' && state != ParseState.IN_BRACKET) {
                state = ParseState.SEEN_DOLLAR;
                continue;
            }

            if (c == '{' && state == ParseState.SEEN_DOLLAR) {
                buffer.append(string.substring(start, i - 1));
                state = ParseState.IN_BRACKET;
                start = i - 1;
                continue;
            }

            if (state == ParseState.SEEN_DOLLAR) {
                state = ParseState.NORMAL;
                continue;
            }

            if (c != '}' || state != ParseState.IN_BRACKET) {
                continue;
            }

            if (start + 2 == i) {
                buffer.append("${}");
                start = i + 1;
                state = ParseState.NORMAL;
                continue;
            }

            String value = null;
            String key = string.substring(start + 2, i);
            if (FILE_SEPARATOR_ALIAS.equals(key)) {
                value = FILE_SEPARATOR;
            } else if (PATH_SEPARATOR_ALIAS.equals(key)) {
                value = PATH_SEPARATOR;
            } else {
                value = resolveKey(key, props);
            }

            if (value != null) {
                properties = true;
                buffer.append(replaceProperties(value, props));
            } else {
                buffer.append("${");
                buffer.append(key);
                buffer.append('}');
            }
            start = i + 1;
            state = ParseState.NORMAL;
        }

        if (!properties) {
            return string;
        }

        if (start != chars.length) {
            buffer.append(string.substring(start, chars.length));
        }

        return buffer.toString();
    }

    private static String resolveSimple(final String key,
            final Map<String, String> props) {
        return AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                String value = null;
                if (props != null) {
                    value = props.get(key);
                }
                if (value != null) {
                    return value;
                }
                value = System.getenv(key);
                if (value != null) {
                    return value;
                }
                return System.getProperty(key);
            }
        });
    }

    private static String resolveKey(final String key,
            final Map<String, String> props) {
        String value = resolveSimple(key, props);
        if (value != null) {
            return value;
        }
        int colon = key.indexOf(':');
        if (colon <= 0) {
            return resolveCompositeKey(key, props);
        }
        String realKey = key.substring(0, colon);
        value = resolveSimple(realKey, props);
        if (value != null) {
            return value;
        }
        value = resolveCompositeKey(realKey, props);
        if (value == null) {
            value = key.substring(colon + 1);
        }
        return value;
    }

    private static String resolveCompositeKey(final String key,
            final Map<String, String> props) {
        String value = null;
        int comma = key.indexOf(',');
        if (comma <= -1) {
            return null;
        }
        if (comma > 0) {
            String key1 = key.substring(0, comma);
            value = resolveSimple(key1, props);
        }
        if (value == null && comma < key.length() - 1) {
            String key2 = key.substring(comma + 1);
            value = resolveSimple(key2, props);
        }
        return value;
    }

    static final class Log4jSilencer {

        private Log4jSilencer() {
            super();
        }

        public static <T> T invoke(
                final ISessionInvoker<T, Void, RuntimeException> invoker,
                final Class<?>... classes) {
            Map<Class<?>, Level> levels = new HashMap<>();
            try {
                for (Class<?> clazz : classes) {
                    levels.put(clazz, switchLogLevel(clazz, Level.OFF));
                }
                return invoker.invoke(null);
            } finally {
                for (Map.Entry<Class<?>, Level> entry : levels.entrySet()) {
                    switchLogLevel(entry.getKey(), entry.getValue());
                }
            }
        }

        private static Level switchLogLevel(final Class<?> clazz,
                final Level newLevel) {
            org.apache.log4j.Logger logger = DfLogger.getLogger(clazz);
            Level old = logger.getLevel();
            logger.setLevel(newLevel);
            return old;
        }

    }

}
