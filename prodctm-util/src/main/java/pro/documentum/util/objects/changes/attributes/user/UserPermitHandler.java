package pro.documentum.util.objects.changes.attributes.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;

import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class UserPermitHandler extends AbstractUserAttributeHandler {

    public static final Set<String> PERMIT_ATTRS;

    static {
        Set<String> permitAttributes = new HashSet<>();
        permitAttributes.add("owner_def_permit");
        permitAttributes.add("group_def_permit");
        permitAttributes.add("world_def_permit");
        PERMIT_ATTRS = Collections.unmodifiableSet(permitAttributes);
    }

    public UserPermitHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, PERMIT_ATTRS);
    }

    @Override
    protected boolean doApply(final IDfUser user, final Map<String, ?> values)
        throws DfException {
        for (String attrName : PERMIT_ATTRS) {
            Integer permit = (Integer) values.remove(attrName);
            if (permit == null || permit < 1) {
                continue;
            }
            Logger.debug("Setting {0} value of object {1} to {2}", attrName,
                    user.getObjectId(), permit);
            user.setInt(attrName, permit);
        }
        return false;
    }

}
