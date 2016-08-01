package pro.documentum.util.objects.changes.attributes.content;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        Set<String> parentAttributes = new HashSet<>();
        parentAttributes.add("parent_id");
        parentAttributes.add("page");
        parentAttributes.add("page_modifier");
        PARENT_ATTRS = Collections.unmodifiableSet(parentAttributes);
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
    public boolean doApply(final IDfContent content, final Map<String, ?> values)
        throws DfException {
        List<IDfId> parentIds = (List<IDfId>) values.remove("parent_id");
        List<Integer> pageNos = (List<Integer>) values.remove("page");
        List<String> pageModifiers = (List<String>) values
                .remove("page_modifier");
        checkConsistency(parentIds, pageNos, pageModifiers);
        DfObjects.unlinkAllParents(content);
        for (int i = 0, n = parentIds.size(); i < n; i++) {
            DfObjects.link(content, parentIds.get(i), pageNos.get(i),
                    pageModifiers.get(i));
        }
        return false;
    }

    private void checkConsistency(final List<IDfId> parentIds,
            final List<Integer> pageNos, final List<String> pageModifiers) {
        if (parentIds == null && pageNos == null && pageModifiers == null) {
            return;
        }
        Objects.requireNonNull(parentIds, "parents are null");
        Objects.requireNonNull(pageNos, "pages are null");
        Objects.requireNonNull(pageModifiers, "pages are null");
        if (parentIds.size() == pageNos.size()
                && pageNos.size() == pageModifiers.size()) {
            return;
        }
        throw new IllegalArgumentException("Wrong size");
    }

}
