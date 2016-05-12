package pro.documentum.util.objects.changes.attributes.content;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.content.IDfContent;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

import pro.documentum.util.objects.DfObjects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ParentIdHandler extends AbstractContentAttributeHandler {

    public static final Set<String> PARENT_ATTRS;

    static {
        PARENT_ATTRS = new HashSet<String>();
        PARENT_ATTRS.add("parent_id");
        PARENT_ATTRS.add("page");
        PARENT_ATTRS.add("page_modifier");
    }

    public ParentIdHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, PARENT_ATTRS);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean doApply(final IDfContent object, final Map<String, ?> values)
        throws DfException {
        List<IDfId> parentIds = (List<IDfId>) values.remove("parent_id");
        List<Integer> pageNos = (List<Integer>) values.remove("page");
        List<String> pageModifiers = (List<String>) values
                .remove("page_modifier");
        checkConsistency(parentIds, pageNos, pageModifiers);
        DfObjects.unlinkAllParents(object);
        if (parentIds == null) {
            return false;
        }
        for (int i = 0, n = parentIds.size(); i < n; i++) {
            DfObjects.link(object, parentIds.get(i), pageNos.get(i),
                    pageModifiers.get(i));
        }
        return false;
    }

    private void checkConsistency(final List<IDfId> parentIds,
            final List<Integer> pageNos, final List<String> pageModifiers) {
        if (parentIds == null && pageNos == null && pageModifiers == null) {
            return;
        }
        if (parentIds == null) {
            throw new IllegalArgumentException("parents are null");
        }
        if (pageNos == null) {
            throw new IllegalArgumentException("pages are null");
        }
        if (pageModifiers == null) {
            throw new IllegalArgumentException("page modifiers are null");
        }
        if (parentIds.size() == pageNos.size()
                && pageNos.size() == pageModifiers.size()) {
            return;
        }
        throw new IllegalArgumentException("Wrong size");
    }

}
