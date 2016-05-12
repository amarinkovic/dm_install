package pro.documentum.util.objects.changes.attributes.persistent;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.aspect.IDfAspects;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfList;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AspectNameHandler extends
        AbstractPersistentAttributeHandler<IDfPersistentObject> {

    public AspectNameHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, "r_aspect_name");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean doApply(final IDfPersistentObject object,
            final Map<String, ?> values) throws DfException {
        List<String> aspects = (List<String>) values.remove("r_aspect_name");
        IDfList current = ((IDfAspects) object).getAspects();
        for (int i = current.getCount() - 1; i >= 0; i--) {
            String aspect = current.getString(i);
            if (!aspects.contains(aspect)) {
                ((IDfAspects) object).detachAspect(aspect, null);
            }
        }
        for (String aspect : aspects) {
            if (current.findStringIndex(aspect) > -1) {
                continue;
            }
            ((IDfAspects) object).attachAspect(aspect, null);
        }
        return false;
    }

}
