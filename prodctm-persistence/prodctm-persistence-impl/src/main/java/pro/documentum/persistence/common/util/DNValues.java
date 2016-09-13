package pro.documentum.persistence.common.util;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.util.convert.Converter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNValues {

    private static final Converter CONVERTER = Converter.getInstance();

    private DNValues() {
        super();
    }

    public static int getValueCount(final IDfTypedObject object,
            final String attrName) {
        try {
            return object.getValueCount(attrName);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static boolean hasAttr(final IDfTypedObject object,
            final String attrName) {
        try {
            return object.hasAttr(attrName);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static String getObjectId(final IDfTypedObject object) {
        try {
            // the general concept is to call
            // getString(object, "r_object_id")
            // but it is too slow.
            return object.getObjectId().getId();
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    public static String getString(final IDfTypedObject object,
            final String attrName) {
        return getSingleValue(object, attrName, String.class);
    }

    public static <T> T getSingleValue(final IDfTypedObject object,
            final String attrName, final Class<?> type) {
        return getSingleValue(object, attrName, 0, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSingleValue(final IDfTypedObject object,
            final String attrName, final int index, final Class<?> type) {
        try {
            return (T) CONVERTER.fromDataStore(object, attrName, type, index);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

}
