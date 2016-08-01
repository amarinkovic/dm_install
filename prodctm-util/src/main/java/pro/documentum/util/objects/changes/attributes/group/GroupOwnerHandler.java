package pro.documentum.util.objects.changes.attributes.group;

import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class GroupOwnerHandler extends AbstractGroupAttributeHandler {

    public GroupOwnerHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, "owner_name");
    }

    @Override
    public boolean doApply(final IDfGroup object, final Map<String, ?> values)
        throws DfException {
        object.setOwnerName((String) values.remove("owner_name"));
        return false;
    }

}
