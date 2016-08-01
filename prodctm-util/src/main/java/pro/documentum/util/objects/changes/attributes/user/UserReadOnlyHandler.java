package pro.documentum.util.objects.changes.attributes.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class UserReadOnlyHandler extends AbstractUserAttributeHandler {

    public static final Set<String> READONLY_ATTRS;

    static {
        Set<String> readOnlyAttributes = new HashSet<>();
        readOnlyAttributes.add("r_is_group");
        READONLY_ATTRS = Collections.unmodifiableSet(readOnlyAttributes);
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
    public boolean doApply(final IDfUser user, final Map<String, ?> values)
        throws DfException {
        removeKey(values, READONLY_ATTRS);
        return false;
    }

    @Override
    protected boolean isReadOnly() {
        return true;
    }

}
