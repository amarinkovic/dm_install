package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class SysObjectReadOnlyHandler extends
        AbstractSysObjectAttributeHandler {

    public static final Set<String> READONLY_ATTRS;

    static {
        READONLY_ATTRS = new HashSet<String>();
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
        return containsKey(attrNames, READONLY_ATTRS);
    }

    @Override
    public boolean doApply(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        removeKey(values, READONLY_ATTRS);
        return false;
    }

    @Override
    protected boolean isReadOnly() {
        return true;
    }

}
