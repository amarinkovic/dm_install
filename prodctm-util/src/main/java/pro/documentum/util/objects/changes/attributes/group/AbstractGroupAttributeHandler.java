package pro.documentum.util.objects.changes.attributes.group;

import java.util.Set;

import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.common.DfException;

import pro.documentum.util.objects.changes.attributes.Depends;
import pro.documentum.util.objects.changes.attributes.persistent.AbstractPersistentAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Depends(on = {GroupReadOnlyHandler.class })
public abstract class AbstractGroupAttributeHandler extends
        AbstractPersistentAttributeHandler<IDfGroup> {

    @Override
    public boolean accept(final Object object, final Set<String> attrNames)
        throws DfException {
        if (object instanceof IDfGroup) {
            return doAccept(attrNames);
        }
        return false;
    }

    protected abstract boolean doAccept(final Set<String> attrNames);

}
