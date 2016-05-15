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
    public boolean doApply(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        String value = (String) values.remove("object_name");
        Logger.debug("Setting object_name of {0} to {1}", object.getObjectId(),
                value);
        object.setObjectName(value);
        return false;
    }

}
