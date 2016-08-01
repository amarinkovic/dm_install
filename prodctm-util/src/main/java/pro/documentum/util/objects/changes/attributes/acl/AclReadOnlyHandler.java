package pro.documentum.util.objects.changes.attributes.acl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class AclReadOnlyHandler extends AbstractAclHandler {

    public static final Set<String> READONLY_ATTRS;

    static {
        Set<String> readOnlyAttributes = new HashSet<>();
        readOnlyAttributes.add("r_is_group");
        READONLY_ATTRS = Collections.unmodifiableSet(readOnlyAttributes);
    }

    public AclReadOnlyHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, READONLY_ATTRS);
    }

    @Override
    public boolean doApply(final IDfACL acl, final Map<String, ?> values)
        throws DfException {
        removeKey(values, READONLY_ATTRS);
        return false;
    }

    @Override
    protected boolean isReadOnly() {
        return true;
    }

}
