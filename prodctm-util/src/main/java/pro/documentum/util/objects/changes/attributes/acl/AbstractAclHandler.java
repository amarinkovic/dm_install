package pro.documentum.util.objects.changes.attributes.acl;

import java.util.Set;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.common.DfException;

import pro.documentum.util.objects.changes.attributes.Depends;
import pro.documentum.util.objects.changes.attributes.persistent.AbstractPersistentAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Depends(on = {AclReadOnlyHandler.class })
public abstract class AbstractAclHandler extends
        AbstractPersistentAttributeHandler<IDfACL> {

    @Override
    public boolean accept(final Object object, final Set<String> attrNames)
        throws DfException {
        if (object instanceof IDfACL) {
            return doAccept(attrNames);
        }
        return false;
    }

    protected abstract boolean doAccept(final Set<String> attrNames);

}
