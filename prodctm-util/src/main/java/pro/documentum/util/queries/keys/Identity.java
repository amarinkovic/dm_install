package pro.documentum.util.queries.keys;

import com.documentum.fc.common.DfDocbaseConstants;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class Identity extends CompositeKey {

    public Identity(final String objectId) {
        super();
        doAdd(DfDocbaseConstants.R_OBJECT_ID, objectId);
    }

    @Override
    public boolean isIdentity() {
        return true;
    }

    @Override
    public boolean isOnlyAttr(final String attrName) {
        return DfDocbaseConstants.R_OBJECT_ID.equals(attrName);
    }

    @Override
    public Identity add(final String column, final Object value) {
        throw new IllegalArgumentException(
                "You can't add extra values for identity");
    }

}
