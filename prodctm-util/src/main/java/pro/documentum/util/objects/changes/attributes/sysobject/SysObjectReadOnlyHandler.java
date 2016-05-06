package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;

import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class SysObjectReadOnlyHandler extends
        AbstractSysObjectAttributeHandler {

    public static final Set<String> READONLY_ATTRS;

    static {
        READONLY_ATTRS = new HashSet<String>();
        READONLY_ATTRS.add(DfDocbaseConstants.R_OBJECT_ID);
        READONLY_ATTRS.add(DfDocbaseConstants.I_VSTAMP);
        READONLY_ATTRS.add("r_creation_date");
        READONLY_ATTRS.add("r_creator_name");
        READONLY_ATTRS.add("r_modify_date");
        READONLY_ATTRS.add("r_modifier");
    }

    public SysObjectReadOnlyHandler() {
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
    public boolean doApply(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        for (String attrName : READONLY_ATTRS) {
            if (!values.containsKey(attrName)) {
                continue;
            }
            Logger.debug("Attempt to set readonly attribute: {0}",
                    new Exception(), attrName);
            values.remove(attrName);
        }
        return false;
    }

}
