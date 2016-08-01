package pro.documentum.util.objects.changes.attributes.group;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class GroupReadOnlyHandler extends AbstractGroupAttributeHandler {

    public static final Set<String> READONLY_ATTRS;

    static {
        READONLY_ATTRS = new HashSet<>();
        READONLY_ATTRS.add(DfDocbaseConstants.R_OBJECT_ID);
        READONLY_ATTRS.add(DfDocbaseConstants.I_VSTAMP);
        READONLY_ATTRS.add("r_modify_date");
        READONLY_ATTRS.add("r_has_events");
        READONLY_ATTRS.add("i_all_users_names");
        READONLY_ATTRS.add("i_supergroups_names");
        READONLY_ATTRS.add("i_nondyn_supergroups_names");
    }

    public GroupReadOnlyHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        for (String attrName : READONLY_ATTRS) {
            if (attrNames.contains(attrName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean doApply(final IDfGroup object, final Map<String, ?> values)
        throws DfException {
        removeKey(values, READONLY_ATTRS);
        return false;
    }

    @Override
    protected boolean isReadOnly() {
        return true;
    }

}
