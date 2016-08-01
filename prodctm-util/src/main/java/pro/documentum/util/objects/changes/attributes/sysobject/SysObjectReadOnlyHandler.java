package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.Collections;
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
        Set<String> readOnlyAttributes = new HashSet<>();
        readOnlyAttributes.add("r_creation_date");
        readOnlyAttributes.add("r_creator_name");
        readOnlyAttributes.add("r_modifier");
        readOnlyAttributes.add("r_lock_date");
        readOnlyAttributes.add("r_lock_machine");
        readOnlyAttributes.add("r_object_type");
        READONLY_ATTRS = Collections.unmodifiableSet(readOnlyAttributes);
    }

    public SysObjectReadOnlyHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, READONLY_ATTRS);
    }

    @Override
    public boolean doApply(final IDfSysObject sysObject,
            final Map<String, ?> values) throws DfException {
        removeKey(values, READONLY_ATTRS);
        return false;
    }

    @Override
    protected boolean isReadOnly() {
        return true;
    }

}
