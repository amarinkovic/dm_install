package pro.documentum.util.objects.changes.attributes.group;

import java.util.Collections;
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
        Set<String> readOnlyAttributes = new HashSet<>();
        readOnlyAttributes.add(DfDocbaseConstants.R_OBJECT_ID);
        readOnlyAttributes.add(DfDocbaseConstants.I_VSTAMP);
        readOnlyAttributes.add("i_all_users_names");
        readOnlyAttributes.add("i_supergroups_names");
        readOnlyAttributes.add("i_nondyn_supergroups_names");
        READONLY_ATTRS = Collections.unmodifiableSet(readOnlyAttributes);
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
    public boolean doApply(final IDfGroup group, final Map<String, ?> values)
        throws DfException {
        removeKey(values, READONLY_ATTRS);
        return false;
    }

    @Override
    protected boolean isReadOnly() {
        return true;
    }

}
