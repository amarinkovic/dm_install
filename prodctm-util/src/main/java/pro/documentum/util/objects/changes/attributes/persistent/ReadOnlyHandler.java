package pro.documentum.util.objects.changes.attributes.persistent;

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
        READONLY_ATTRS = new HashSet<String>();
        READONLY_ATTRS.add(DfDocbaseConstants.R_OBJECT_ID);
        READONLY_ATTRS.add(DfDocbaseConstants.I_VSTAMP);
    }

    public ReadOnlyHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, READONLY_ATTRS);
    }

    @Override
    public boolean doApply(final IDfPersistentObject object,
            final Map<String, ?> values) throws DfException {
        removeKey(values, READONLY_ATTRS);
        return false;
    }

}
