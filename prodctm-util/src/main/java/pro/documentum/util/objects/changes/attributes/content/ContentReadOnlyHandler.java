package pro.documentum.util.objects.changes.attributes.content;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.content.IDfContent;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ContentReadOnlyHandler extends AbstractContentAttributeHandler {

    public static final Set<String> READONLY_ATTRS;

    static {
        Set<String> readOnlyAttributes = new HashSet<>();
        readOnlyAttributes.add("data_ticket");
        readOnlyAttributes.add("other_ticket");
        readOnlyAttributes.add("content_size");
        readOnlyAttributes.add("full_content_size");
        readOnlyAttributes.add("set_time");
        READONLY_ATTRS = Collections.unmodifiableSet(readOnlyAttributes);
    }

    public ContentReadOnlyHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, READONLY_ATTRS);
    }

    @Override
    public boolean doApply(final IDfContent content, final Map<String, ?> values)
        throws DfException {
        removeKey(values, READONLY_ATTRS);
        return false;
    }

    @Override
    protected boolean isReadOnly() {
        return true;
    }

}
