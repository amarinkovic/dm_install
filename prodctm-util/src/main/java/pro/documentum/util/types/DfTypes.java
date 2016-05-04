package pro.documentum.util.types;

import java.util.ArrayList;
import java.util.List;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.impl.typeddata.ILiteType;
import com.documentum.fc.client.impl.typeddata.MissingAttributeException;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DfTypes {

    public static final String I_POSITION;

    static {
        I_POSITION = "i_position";
    }

    public static final List<String> SPECIAL_ATTRS = new ArrayList<String>();

    static {
        SPECIAL_ATTRS.add(DfDocbaseConstants.R_OBJECT_ID);
        SPECIAL_ATTRS.add(I_POSITION);
        SPECIAL_ATTRS.add(DfDocbaseConstants.I_VSTAMP);
    }

    private DfTypes() {
        super();
    }

    public static List<String> getAttributes(final IDfSession session,
            final String typeName) throws DfException {
        return getAttributes(session.getType(typeName));
    }

    public static List<String> getAttributes(final IDfType type)
        throws DfException {
        final int attrCount = type.getTypeAttrCount();
        List<String> result = new ArrayList<String>(attrCount
                + SPECIAL_ATTRS.size());
        result.addAll(SPECIAL_ATTRS);
        boolean hasRepeating = false;
        for (int i = 0; i < attrCount; i++) {
            IDfAttr attr = type.getTypeAttr(i);
            String attrName = attr.getName();
            if (SPECIAL_ATTRS.contains(attrName)) {
                continue;
            }
            result.add(attrName);
            if (attr.isRepeating()) {
                hasRepeating = true;
            }
        }
        result.add(DfDocbaseConstants.R_OBJECT_ID);
        result.add(DfDocbaseConstants.I_VSTAMP);
        if (!hasRepeating) {
            result.remove(I_POSITION);
        }
        return result;
    }

    public static void verifyAttrIsPresent(final String attrName,
            final IDfTypedObject object, final ILiteType type)
        throws DfException {
        if (type.hasAttr(attrName) && !object.hasAttr(attrName)) {
            throw new MissingAttributeException(attrName);
        }
    }

    public static void verifyAttrIsPresent(final String attrName,
            final IDfTypedObject object, final IDfType type) throws DfException {
        if (type.findTypeAttrIndex(attrName) > 0 && !object.hasAttr(attrName)) {
            throw new MissingAttributeException(attrName);
        }
    }

}
