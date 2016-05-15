package pro.documentum.util.objects.changes.attributes.content;

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
        READONLY_ATTRS = new HashSet<String>();
        READONLY_ATTRS.add("data_ticket");
        READONLY_ATTRS.add("other_ticket");
        READONLY_ATTRS.add("content_size");
        READONLY_ATTRS.add("full_content_size");
        READONLY_ATTRS.add("set_time");
    }

    public ContentReadOnlyHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, READONLY_ATTRS);
    }

    @Override
    public boolean doApply(final IDfContent object, final Map<String, ?> values)
        throws DfException {
        removeKey(values, READONLY_ATTRS);
        return false;
    }

    @Override
    protected boolean isReadOnly() {
        return true;
    }

}
