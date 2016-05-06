package pro.documentum.util.objects.changes.attributes.workitem;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfList;
import com.documentum.fc.common.IDfList;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class OutputPortHandler extends AbstractWorkItemAttributeHandler {

    public OutputPortHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return attrNames.contains("r_port_name");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean doApply(final IDfWorkitem object, final Map<String, ?> values)
        throws DfException {
        IDfList list = new DfList(IDfList.DF_STRING);
        List<String> outputPorts = (List<String>) values.remove("r_port_name");
        for (String value : outputPorts) {
            list.append(value);
        }
        object.setOutput(list);
        return false;
    }

}
