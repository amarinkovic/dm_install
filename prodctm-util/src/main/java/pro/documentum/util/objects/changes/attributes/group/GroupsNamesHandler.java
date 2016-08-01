package pro.documentum.util.objects.changes.attributes.group;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class GroupsNamesHandler extends AbstractGroupAttributeHandler {

    public GroupsNamesHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, "groups_names");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean doApply(final IDfGroup object, final Map<String, ?> values)
        throws DfException {
        List<String> groups = (List<String>) values.remove("groups_names");
        object.removeAllGroups();
        if (groups == null) {
            return false;
        }
        for (String groupName : groups) {
            object.addGroup(groupName);
        }
        return false;
    }

}
