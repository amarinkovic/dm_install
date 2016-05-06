package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

import pro.documentum.util.objects.changes.attributes.Depends;
import pro.documentum.util.objects.changes.attributes.persistent.AbstractPersistentAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Depends(on = {VersionHandler.class, SysObjectReadOnlyHandler.class })
public abstract class AbstractSysObjectAttributeHandler extends
        AbstractPersistentAttributeHandler<IDfSysObject> {

    @Override
    public boolean accept(final Object object, final Set<String> attrNames)
        throws DfException {
        if (object instanceof IDfSysObject) {
            return doAccept(attrNames);
        }
        return false;
    }

    protected abstract boolean doAccept(final Set<String> attrNames);

}
