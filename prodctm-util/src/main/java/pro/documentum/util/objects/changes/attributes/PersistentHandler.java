package pro.documentum.util.objects.changes.attributes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;

import pro.documentum.util.logger.Logger;
import pro.documentum.util.objects.DfObjects;
import pro.documentum.util.objects.changes.attributes.sysobject.VersionHandler;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistentHandler implements
        IAttributeHandler<IDfPersistentObject> {

    public static final Set<String> IGNORE_ATTRIBUTES;

    static {
        Set<String> ignoreAttributes = new HashSet<>();
        ignoreAttributes.addAll(VersionHandler.VERSION_ATTRIBUTES);
        IGNORE_ATTRIBUTES = Collections.unmodifiableSet(ignoreAttributes);
    }

    public PersistentHandler() {
        super();
    }

    @Override
    public List<Class<? extends IAttributeHandler<?>>> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public boolean apply(final IDfPersistentObject object,
            final Map<String, ?> values) throws DfException {
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, ?> e : values.entrySet()) {
            String attrName = e.getKey();
            if (ignore(attrName)) {
                continue;
            }
            toRemove.add(attrName);
            Object value = e.getValue();
            Logger.debug("Setting {0} value of object {1} to {2}", attrName,
                    object.getObjectId(), value);
            int dataType = object.getAttrDataType(attrName);
            if (!object.isAttrRepeating(attrName)) {
                DfObjects.setValue(object, attrName, value, dataType, 0);
                continue;
            }

            object.removeAll(attrName);

            if (value instanceof Collection) {
                int index = 0;
                for (Object rValue : (Collection) value) {
                    DfObjects.setValue(object, attrName, rValue, dataType, index);
                    index++;
                }
            }

            if (value.getClass().isArray()) {
                for (int i = 0, n = Array.getLength(value); i < n; i++) {
                    DfObjects.setValue(object, attrName, Array.get(value, i), dataType, i);
                }
            }
        }

        for (String attrName : toRemove) {
            values.remove(attrName);
        }
        return false;
    }

    private boolean ignore(final String attrName) {
        return IGNORE_ATTRIBUTES.contains(attrName);
    }

    @Override
    public boolean accept(final Object object, final Set<String> attrNames)
        throws DfException {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getName();
    }

}
