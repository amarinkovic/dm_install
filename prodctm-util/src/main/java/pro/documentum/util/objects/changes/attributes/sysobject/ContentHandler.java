package pro.documentum.util.objects.changes.attributes.sysobject;

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
        PRIMARY_CONTENT_ATTRS = new HashSet<String>();
        PRIMARY_CONTENT_ATTRS.add("i_contents_id");
        PRIMARY_CONTENT_ATTRS.add("r_content_size");
        PRIMARY_CONTENT_ATTRS.add("r_full_content_size");
        PRIMARY_CONTENT_ATTRS.add("a_content_type");
        PRIMARY_CONTENT_ATTRS.add("a_storage_type");
    }

    public ContentHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, PRIMARY_CONTENT_ATTRS);
    }

    @Override
    protected boolean doApply(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        return false;
    }

}
