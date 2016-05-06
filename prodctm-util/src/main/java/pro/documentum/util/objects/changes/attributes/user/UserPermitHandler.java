package pro.documentum.util.objects.changes.attributes.user;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class UserPermitHandler extends AbstractUserAttributeHandler {

    public static final Set<String> PERMIT_ATTRS;

    static {
        PERMIT_ATTRS = new HashSet<String>();
        PERMIT_ATTRS.add("owner_def_permit");
        PERMIT_ATTRS.add("group_def_permit");
        PERMIT_ATTRS.add("world_def_permit");
    }

    public UserPermitHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        for (String attrName : PERMIT_ATTRS) {
            if (attrNames.contains(attrName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean doApply(final IDfUser object, final Map<String, ?> values)
        throws DfException {
        for (String attrName : PERMIT_ATTRS) {
            Integer permit = (Integer) values.remove(attrName);
            if (permit == null || permit < 1) {
                continue;
            }
            object.setInt(attrName, permit);
        }
        return false;
    }

}
