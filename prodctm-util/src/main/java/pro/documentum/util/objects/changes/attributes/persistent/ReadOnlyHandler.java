package pro.documentum.util.objects.changes.attributes.persistent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ReadOnlyHandler extends
        AbstractPersistentAttributeHandler<IDfPersistentObject> {

    public static final Set<String> READONLY_ATTRS;

    static {
        Set<String> readOnlyAttributes = new HashSet<>();
        readOnlyAttributes.add(DfDocbaseConstants.R_OBJECT_ID);
        readOnlyAttributes.add(DfDocbaseConstants.I_VSTAMP);
        readOnlyAttributes.add("r_has_events");
        readOnlyAttributes.add("r_modify_date");
        READONLY_ATTRS = Collections.unmodifiableSet(readOnlyAttributes);
    }

    public ReadOnlyHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, READONLY_ATTRS);
    }

    @Override
    public boolean doApply(final IDfPersistentObject persistentObject,
            final Map<String, ?> values) throws DfException {
        removeKey(values, READONLY_ATTRS);
        return false;
    }

    @Override
    protected boolean isReadOnly() {
        return true;
    }

}
