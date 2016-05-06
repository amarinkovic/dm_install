package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PolicyHandler extends AbstractSysObjectAttributeHandler {

    private static final List<String> POLICY_ATTRIBUTES;

    static {
        POLICY_ATTRIBUTES = new ArrayList<String>();
        POLICY_ATTRIBUTES.add("r_policy_id");
        POLICY_ATTRIBUTES.add("r_current_state");
    }

    public PolicyHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        for (String attrName : POLICY_ATTRIBUTES) {
            if (attrNames.contains(attrName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean doApply(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        IDfId newPolicy = (IDfId) values.remove("r_policy_id");
        Integer newState = (Integer) values.remove("r_current_state");
        if (newPolicy.isNull()) {
            object.detachPolicy();
        }
        return false;
    }

}
