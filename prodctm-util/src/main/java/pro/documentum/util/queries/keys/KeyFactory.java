package pro.documentum.util.queries.keys;

import java.util.List;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class KeyFactory {

    private KeyFactory() {
        super();
    }

    @SuppressWarnings("unchecked")
    public static <T extends CompositeKey> T createKey(
            final IDfTypedObject object, final List<String> attributes)
        throws DfException {
        CompositeKey key;
        if (isIdentity(attributes)) {
            key = new Identity(object.getObjectId().getId());
        } else {
            key = new CompositeKey();
            for (String attrName : attributes) {
                key.add(attrName, object.getString(attrName));
            }
        }
        return (T) key;
    }

    public static boolean isIdentity(final List<String> attributes) {
        if (attributes.size() != 1) {
            return false;
        }
        return DfDocbaseConstants.R_OBJECT_ID.equals(attributes.get(0));
    }

}
