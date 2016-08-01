package pro.documentum.util.objects.changes.attributes.acl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfPermit;
import com.documentum.fc.common.DfException;

import pro.documentum.util.objects.DfObjects;
import pro.documentum.util.permits.PermitConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AclPermitHandler extends AbstractAclHandler {

    public static final Set<String> PERMIT_ATTRIBUTES;

    static {
        Set<String> permitAttributes = new HashSet<>();
        permitAttributes.add("r_accessor_name");
        permitAttributes.add("r_accessor_permit");
        permitAttributes.add("r_accessor_xpermit");
        permitAttributes.add("r_permit_type");
        permitAttributes.add("r_application_permit");
        PERMIT_ATTRIBUTES = Collections.unmodifiableSet(permitAttributes);
    }

    public AclPermitHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, PERMIT_ATTRIBUTES);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean doApply(final IDfACL acl, final Map<String, ?> values)
        throws DfException {
        List<String> accessors = removeKey(values, "r_accessor_name");
        List<Integer> accessPermits = removeKey(values, "r_accessor_permit");
        List<Integer> xPermits = removeKey(values, "r_accessor_xpermit");
        List<Integer> permitTypes = removeKey(values, "r_permit_type");
        List<String> appPermits = removeKey(values, "r_application_permit");
        DfObjects.resetAcl(acl);
        for (int i = 0, n = accessors.size(); i < n; i++) {
            applyPermit(acl, permitTypes.get(i), accessors.get(i),
                    accessPermits.get(i), xPermits.get(i), appPermits.get(i));
        }
        return false;
    }

    private void applyPermit(final IDfACL acl, final Integer permitType,
            final String accessorName, final Integer accessPermit,
            final Integer xPermit, final String applicationPermit)
        throws DfException {
        for (IDfPermit permit : PermitConverter.createPermits(permitType,
                accessorName, accessPermit, xPermit, applicationPermit)) {
            acl.grantPermit(permit);
        }
    }

}
