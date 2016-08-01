package pro.documentum.util.objects.changes.attributes.group;

import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class GroupNameHandler extends AbstractGroupAttributeHandler {

    public GroupNameHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, "group_name");
    }

    @Override
    public boolean doApply(final IDfGroup group, final Map<String, ?> values)
        throws DfException {
        group.setGroupName((String) values.remove("group_name"));
        return false;
    }

}
