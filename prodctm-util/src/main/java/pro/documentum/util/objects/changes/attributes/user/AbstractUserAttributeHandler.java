package pro.documentum.util.objects.changes.attributes.user;

import java.util.Set;

import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;

import pro.documentum.util.objects.changes.attributes.Depends;
import pro.documentum.util.objects.changes.attributes.persistent.AbstractPersistentAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Depends(on = {UserReadOnlyHandler.class })
public abstract class AbstractUserAttributeHandler extends
        AbstractPersistentAttributeHandler<IDfUser> {

    @Override
    public boolean accept(final Object object, final Set<String> attrNames)
        throws DfException {
        if (object instanceof IDfUser) {
            return doAccept(attrNames);
        }
        return false;
    }

    protected abstract boolean doAccept(final Set<String> attrNames);

}
