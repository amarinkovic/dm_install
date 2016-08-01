package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ContentHandler extends AbstractSysObjectAttributeHandler {

    public static final Set<String> PRIMARY_CONTENT_ATTRS;

    static {
        Set<String> primaryContentAttributes = new HashSet<>();
        primaryContentAttributes.add("i_contents_id");
        primaryContentAttributes.add("r_content_size");
        primaryContentAttributes.add("r_full_content_size");
        primaryContentAttributes.add("a_content_type");
        primaryContentAttributes.add("a_storage_type");
        PRIMARY_CONTENT_ATTRS = Collections
                .unmodifiableSet(primaryContentAttributes);
    }

    public ContentHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, PRIMARY_CONTENT_ATTRS);
    }

    @Override
    protected boolean doApply(final IDfSysObject sysObject,
            final Map<String, ?> values) throws DfException {
        return false;
    }

}
