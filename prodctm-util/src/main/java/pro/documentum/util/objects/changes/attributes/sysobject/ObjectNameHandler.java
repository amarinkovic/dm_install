package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ObjectNameHandler extends AbstractSysObjectAttributeHandler {

    public ObjectNameHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, "object_name");
    }

    @Override
    public boolean doApply(final IDfSysObject sysObject,
            final Map<String, ?> values) throws DfException {
        String value = (String) values.remove("object_name");
        Logger.debug("Setting {0} value of object {1} to {2}", "object_name",
                sysObject.getObjectId(), value);
        sysObject.setObjectName(value);
        return false;
    }

}
