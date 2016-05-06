package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class TitleHandler extends AbstractSysObjectAttributeHandler {

    public TitleHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return attrNames.contains("title");
    }

    @Override
    public boolean doApply(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        object.setTitle((String) values.remove("title"));
        return false;
    }

}
