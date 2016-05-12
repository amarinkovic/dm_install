package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

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
        object.setObjectName((String) values.remove("object_name"));
        return false;
    }

}
