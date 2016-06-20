package pro.documentum.util.objects.changes.attributes.acl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PermitHandler extends AbstractAclPermitHandler {

    public static final Set<String> PERMIT_ATTRIBUTES;

    static {
        PERMIT_ATTRIBUTES = new HashSet<>();
        PERMIT_ATTRIBUTES.add("r_accessor_name");
        PERMIT_ATTRIBUTES.add("r_accessor_permit");
        PERMIT_ATTRIBUTES.add("r_accessor_xpermit");
        PERMIT_ATTRIBUTES.add("r_is_group");
        PERMIT_ATTRIBUTES.add("r_permit_type");
        PERMIT_ATTRIBUTES.add("r_application_permit");
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, PERMIT_ATTRIBUTES);
    }

    @Override
    protected boolean doApply(final IDfACL object, final Map<String, ?> values)
        throws DfException {
        return false;
    }

}
