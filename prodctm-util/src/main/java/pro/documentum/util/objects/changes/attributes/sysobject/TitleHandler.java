package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class TitleHandler extends AbstractSysObjectAttributeHandler {

    public TitleHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, "title");
    }

    @Override
    public boolean doApply(final IDfSysObject sysObject,
            final Map<String, ?> values) throws DfException {
        String title = (String) values.remove("title");
        Logger.debug("Setting {0} value of object {1} to {2}", "title",
                sysObject.getObjectId(), title);
        sysObject.setTitle(title);
        return false;
    }

}
