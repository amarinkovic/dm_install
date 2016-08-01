package pro.documentum.util.objects.changes.attributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfTime;

import pro.documentum.util.logger.Logger;
import pro.documentum.util.objects.changes.attributes.sysobject.VersionHandler;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistentHandler implements
        IAttributeHandler<IDfPersistentObject> {

    public static final Set<String> IGNORE_ATTRIBUTES;

    static {
        IGNORE_ATTRIBUTES = new HashSet<>();
        IGNORE_ATTRIBUTES.addAll(VersionHandler.VERSION_ATTRIBUTES);
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
        for (String attrName : values.keySet()) {
            if (ignore(attrName)) {
                continue;
            }
            toRemove.add(attrName);
            Object value = values.get(attrName);
            Logger.debug("Setting {0} value of object {1} to {2}", attrName,
                    object.getObjectId(), value);
            int dataType = object.getAttrDataType(attrName);
            if (!object.isAttrRepeating(attrName)) {
                setValue(object, attrName, value, dataType, 0);
                continue;
            }
            object.removeAll(attrName);
            if (value instanceof Collection) {
                int index = 0;
                for (Object rValue : (Collection) value) {
                    setValue(object, attrName, rValue, dataType, index);
                    index++;
                }
            }
        }

        for (String attrName : toRemove) {
            values.remove(attrName);
        }
        return false;
    }

    private void setValue(final IDfPersistentObject object,
            final String attrName, final Object value, final int dataType,
            final int index) throws DfException {
        switch (dataType) {
        case IDfAttr.DM_BOOLEAN:
            object.setRepeatingBoolean(attrName, index, (Boolean) value);
            break;
        case IDfAttr.DM_DOUBLE:
            object.setRepeatingDouble(attrName, index, (Double) value);
            break;
        case IDfAttr.DM_INTEGER:
            object.setRepeatingInt(attrName, index, (Integer) value);
            break;
        case IDfAttr.DM_STRING:
            object.setRepeatingString(attrName, index, (String) value);
            break;
        case IDfAttr.DM_ID:
            object.setRepeatingId(attrName, index, (IDfId) value);
            break;
        case IDfAttr.DM_TIME:
            object.setRepeatingTime(attrName, index, (IDfTime) value);
            break;
        default:
            throw new IllegalArgumentException("Invalid datatype: " + dataType);
        }
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
