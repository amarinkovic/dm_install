package pro.documentum.util.objects.changes.attributes.user;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class UserReadOnlyHandler extends AbstractUserAttributeHandler {

    public static final Set<String> READONLY_ATTRS;

    static {
        READONLY_ATTRS = new HashSet<String>();
        READONLY_ATTRS.add(DfDocbaseConstants.R_OBJECT_ID);
        READONLY_ATTRS.add(DfDocbaseConstants.I_VSTAMP);
        READONLY_ATTRS.add("r_modify_date");
        READONLY_ATTRS.add("r_is_group");
    }

    public UserReadOnlyHandler() {
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
    public boolean doApply(final IDfUser object, final Map<String, ?> values)
        throws DfException {
        removeKey(values, READONLY_ATTRS);
        return false;
    }

}
