package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PolicyHandler extends AbstractSysObjectAttributeHandler {

    public static final Set<String> POLICY_ATTRIBUTES;

    static {
        Set<String> policyAttributes = new HashSet<>();
        policyAttributes.add("r_policy_id");
        policyAttributes.add("r_current_state");
        POLICY_ATTRIBUTES = Collections.unmodifiableSet(policyAttributes);
    }

    public PolicyHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, POLICY_ATTRIBUTES);
    }

    @Override
    public boolean doApply(final IDfSysObject sysObject,
            final Map<String, ?> values) throws DfException {
        IDfId newPolicy = (IDfId) values.remove("r_policy_id");
        Integer newState = (Integer) values.remove("r_current_state");
        if (newPolicy.isNull()) {
            sysObject.detachPolicy();
        }
        removeKey(values, POLICY_ATTRIBUTES);
        return false;
    }

}
