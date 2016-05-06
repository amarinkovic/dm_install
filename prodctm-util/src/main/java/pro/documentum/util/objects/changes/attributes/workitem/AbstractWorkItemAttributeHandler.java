package pro.documentum.util.objects.changes.attributes.workitem;

import java.util.Set;

import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfException;

import pro.documentum.util.objects.changes.attributes.persistent.AbstractPersistentAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractWorkItemAttributeHandler extends
        AbstractPersistentAttributeHandler<IDfWorkitem> {

    @Override
    public boolean accept(final Object object, final Set<String> attrNames)
        throws DfException {
        if (object instanceof IDfWorkitem) {
            return doAccept(attrNames);
        }
        return false;
    }

    protected abstract boolean doAccept(final Set<String> attrNames);

}
