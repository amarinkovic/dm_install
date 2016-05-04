package pro.documentum.util.preferences;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfPreferences;
import com.documentum.fc.common.impl.preferences.annotation.BooleanConstraint;
import com.documentum.fc.common.impl.preferences.annotation.DirectoryConstraint;
import com.documentum.fc.common.impl.preferences.annotation.FileConstraint;
import com.documentum.fc.common.impl.preferences.annotation.IntegerConstraint;
import com.documentum.fc.common.impl.preferences.annotation.Preference;
import com.documentum.fc.common.impl.preferences.annotation.StringConstraint;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PreferencesLoader {
    private final Map _persistentProperties;

    public PreferencesLoader(final Map persistentProperties) {
        _persistentProperties = persistentProperties;
    }

    public final void load() {
        load(false);
    }

    public final void load(final boolean initial) {
        Properties filtered = filterKnownProperties(_persistentProperties);
        if (filtered == null || filtered.isEmpty()) {
            return;
        }
        DfPreferences.getInstance().loadProperties(filtered, initial);
    }

    private Properties filterKnownProperties(final Map properties) {
        Properties filtered = new Properties();
        for (Field field : DfPreferences.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Preference.class)) {
                continue;
            }
            filterKnownProperties(field, filtered, properties);
        }
        return filtered;
    }

    private void filterKnownProperties(final Field field,
            final Properties output, final Map input) {
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

        Object preferenceValue = input.get(preferenceName);
        if (preferenceValue == null) {
            return;
        }

        Preference preference = field.getAnnotation(Preference.class);
        boolean repeating = preference.repeating();
        boolean isArray = preferenceValue.getClass().isArray();
        isArray |= preferenceValue instanceof List;

        if (repeating ^ isArray) {
            return;
        }

        if (repeating) {
            filterRepeatingProperty(field, preferenceName, output, input);
        } else {
            filterSingleProperty(field, preferenceName, output, input);
        }

    }

    private void filterRepeatingProperty(final Field field,
            final String preferenceName, final Properties output,
            final Map input) {
        Object preferenceValue = input.get(preferenceName);
        String[] values;
        if (preferenceValue instanceof List) {
            List preferences = (List) preferenceValue;
            if (preferences.isEmpty()) {
                return;
            }
            if (!isCorrectValue(field, preferences.get(0))) {
                return;
            }
            values = convertToStringArray(preferences);
        } else if (preferenceValue.getClass().isArray()) {
            if (Array.getLength(preferenceValue) == 0) {
                return;
            }
            if (!isCorrectValue(field, Array.get(preferenceValue, 0))) {
                return;
            }
            values = convertToStringArray(preferenceValue);
        } else {
            return;
        }
        output.put(preferenceName, values);
    }

    private void filterSingleProperty(final Field field,
            final String preferenceName, final Properties output,
            final Map input) {
        Object preferenceValue = input.get(preferenceName);
        if (isCorrectValue(field, preferenceValue)) {
            output.put(preferenceName, preferenceValue);
        }
    }

    private String[] convertToStringArray(final Object object) {
        if (object instanceof List) {
            List list = (List) object;
            String[] result = new String[list.size()];
            for (int i = 0, n = result.length; i < n; i++) {
                result[i] = convertToString(list.get(i));
            }
            return result;
        }
        if (!object.getClass().isArray()) {
            return new String[] {convertToString(object) };
        }
        String[] result = new String[Array.getLength(object)];
        for (int i = 0, n = result.length; i < n; i++) {
            result[i] = convertToString(Array.get(object, i));
        }
        return result;
    }

    private String convertToString(final Object object) {
        if (object instanceof String) {
            return (String) object;
        }
        return object.toString();
    }

    private boolean isCorrectValue(final Field field,
            final Object preferenceValue) {
        if (field.isAnnotationPresent(StringConstraint.class)
                || field.isAnnotationPresent(DirectoryConstraint.class)
                || field.isAnnotationPresent(FileConstraint.class)) {
            return preferenceValue instanceof String;
        }

        if (field.isAnnotationPresent(IntegerConstraint.class)) {
            if (preferenceValue instanceof Integer) {
                return true;
            }
            if (!(preferenceValue instanceof String)) {
                return false;
            }
            try {
                Integer.parseInt((String) preferenceValue);
                return true;
            } catch (NumberFormatException ex) {
                DfLogger.error(this, ex.getMessage(), null, ex);
            }
            return false;
        }

        if (field.isAnnotationPresent(BooleanConstraint.class)) {
            return preferenceValue instanceof Boolean
                    || preferenceValue instanceof String;
        }

        return false;
    }

}
