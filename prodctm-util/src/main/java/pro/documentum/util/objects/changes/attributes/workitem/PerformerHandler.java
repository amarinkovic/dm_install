package pro.documentum.util.objects.changes.attributes.workitem;

import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PerformerHandler extends AbstractWorkItemAttributeHandler {

    public PerformerHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return attrNames.contains("r_performer_name");
    }

    @Override
    public boolean doApply(final IDfWorkitem object, final Map<String, ?> values)
        throws DfException {
        object.delegateTask((String) values.remove("r_performer_name"));
        return false;
    }

}
