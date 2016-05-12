package pro.documentum.util.objects.changes.attributes.content;

import java.util.Set;

import com.documentum.fc.client.content.IDfContent;
import com.documentum.fc.common.DfException;

import pro.documentum.util.objects.changes.attributes.Depends;
import pro.documentum.util.objects.changes.attributes.persistent.AbstractPersistentAttributeHandler;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Depends(on = {ContentReadOnlyHandler.class })
public abstract class AbstractContentAttributeHandler extends
        AbstractPersistentAttributeHandler<IDfContent> {

    @Override
    public boolean accept(final Object object, final Set<String> attrNames)
        throws DfException {
        if (object instanceof IDfContent) {
            return doAccept(attrNames);
        }
        return false;
    }

    protected abstract boolean doAccept(final Set<String> attrNames);

}
