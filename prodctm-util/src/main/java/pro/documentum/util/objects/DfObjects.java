package pro.documentum.util.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPermit;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.content.IDfContent;
import com.documentum.fc.client.content.impl.IContent;
import com.documentum.fc.client.impl.IPersistentObject;
import com.documentum.fc.client.impl.ISysObject;
import com.documentum.fc.client.impl.ITypedObject;
import com.documentum.fc.client.impl.ObjectIdManager;
import com.documentum.fc.client.impl.objectmanager.IPersistentObjectFactory;
import com.documentum.fc.client.impl.objectmanager.PersistentDataManager;
import com.documentum.fc.client.impl.objectmanager.PersistentObjectManager;
import com.documentum.fc.client.impl.session.ISession;
import com.documentum.fc.client.impl.typeddata.ILiteType;
import com.documentum.fc.client.impl.typeddata.ITypedData;
import com.documentum.fc.client.internal.ISysObjectInternal;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;
import com.documentum.fc.impl.util.reflection.proxy.IProxyHandler;

import pro.documentum.util.constants.DfConstants;
import pro.documentum.util.ids.DfIdUtil;
import pro.documentum.util.permits.PermitConverter;
import pro.documentum.util.types.DfTypes;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DfObjects {

    public static final List<String> MANDATORY_ATTRIBUTES = new ArrayList<>();

    static {
        MANDATORY_ATTRIBUTES.add(DfDocbaseConstants.I_IS_REFERENCE);
        MANDATORY_ATTRIBUTES.add(DfDocbaseConstants.I_VSTAMP);
        MANDATORY_ATTRIBUTES.add(DfDocbaseConstants.I_IS_REPLICA);
        MANDATORY_ATTRIBUTES.add(DfDocbaseConstants.R_ASPECT_NAME);
        MANDATORY_ATTRIBUTES.add("source_docbase");
    }

    private DfObjects() {
        super();
    }

    public static String makeId(final IDfSession dfSession,
            final String typeName) throws DfException {
        return makeIds(dfSession, typeName, 1)[0];
    }

    public static String[] makeIds(final IDfSession dfSession,
            final String typeName, final int howMany) throws DfException {
        String[] result = new String[howMany];
        ISession session = (ISession) dfSession;
        ObjectIdManager objectIdManager = session.getDocbase()
                .getObjectIdManager();
        ILiteType type = session.getLiteType(typeName);
        if (type == null) {
            throw DfException.newBadTypeException(typeName);
        }
        for (int i = 0; i < howMany; i++) {
            result[i] = objectIdManager.getNextId(session, type).getId();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IDfPersistentObject> T getImp(final T object)
        throws DfException {
        IProxyHandler proxyHandler = ((IPersistentObject) object)
                .getProxyHandler();
        if (proxyHandler == null) {
            return object;
        }
        return (T) proxyHandler.____getImp____();
    }

    @SuppressWarnings("unchecked")
    public static <T extends IDfPersistentObject> T buildObject(
            final IDfSession dfSession, final IDfTypedObject data,
            final String typeName) throws DfException {
        ISession session = (ISession) dfSession;
        IDfId objectId = data.getId(DfDocbaseConstants.R_OBJECT_ID);
        int vStamp = data.getInt(DfDocbaseConstants.I_VSTAMP);
        // check cache first

        String objectType = typeName;
        if (data.hasAttr(DfDocbaseConstants.R_OBJECT_TYPE)) {
            objectType = data.getString(DfDocbaseConstants.R_OBJECT_TYPE);
        }

        IPersistentObject object = getObjectFromCache(session, objectId);
        if (object != null) {
            object.setObjectSession(session);
            object.setOriginalSession(session);
            if (object.getVStamp() == vStamp || object.isDirty()) {
                return (T) object;
            }
            objectType = object.getTypeName();
        }

        if (StringUtils.isBlank(objectType)) {
            objectType = DfConstants.getBaseType(objectId);
        }

        if (StringUtils.isBlank(objectType)) {
            throw new IllegalArgumentException(
                    "Unable to determine object type");
        }

        ILiteType type = session.getLiteType(objectType);
        for (String mandatoryAttr : MANDATORY_ATTRIBUTES) {
            DfTypes.verifyAttrIsPresent(mandatoryAttr, data, type);
        }

        PersistentDataManager dataManager = session.getDataManager();
        ITypedData objectData = dataManager.newData(type, objectId,
                DfId.DF_NULLID);
        ITypedData sourceData = ((ITypedObject) data).getData(false);
        objectData.copyValuesFrom(sourceData, null, true);
        objectData.setAutoFill(false);
        return (T) buildObject(session, objectData, true, false);
    }

    public static IPersistentObject getObjectFromCache(
            final IDfSession session, final IDfId objectId) throws DfException {
        PersistentObjectManager objectManager = ((ISession) session)
                .getObjectManager();
        return objectManager.getObjectFromCache(objectId);
    }

    public static <T extends IDfPersistentObject> T newObject(
            final IDfSession dfSession, final String typeName)
        throws DfException {
        return newObject(dfSession, typeName, true);
    }

    public static <T extends IDfPersistentObject> T newUnCached(
            final IDfSession dfSession, final String typeName)
        throws DfException {
        return newObject(dfSession, typeName, false);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IDfPersistentObject> T newObject(
            final IDfSession dfSession, final String typeName,
            final boolean cached) throws DfException {
        ISession session = (ISession) dfSession;
        if (cached) {
            return (T) session.newObject(typeName);
        }
        PersistentObjectManager objectManager = session.getObjectManager();
        return (T) objectManager.newUncachedObject(typeName, null);
    }

    @SuppressWarnings("unchecked")
    private static <T extends IDfPersistentObject> T buildObject(
            final IDfSession dfSession, final ITypedData data,
            final boolean cached, final boolean isNew) throws DfException {
        ISession session = (ISession) dfSession;
        PersistentObjectManager objectManager = session.getObjectManager();
        IPersistentObjectFactory factory = objectManager.getObjectFactory();
        IPersistentObject persistentObject = factory.makeObject(data, isNew,
                session, session);
        if (cached) {
            persistentObject.setCached(true);
            objectManager.replaceObject(persistentObject);
        }
        return (T) persistentObject;
    }

    public static <T extends IDfPersistentObject> T newObject(
            final IDfSession dfSession, final String typeName,
            final String objectId) throws DfException {
        ISession session = (ISession) dfSession;
        PersistentDataManager dataManager = session.getDataManager();
        ILiteType type = session.getLiteType(typeName);
        if (type == null) {
            throw DfException.newBadTypeException(typeName);
        }
        if (type.isAspectAttrDefType()) {
            throw DfException.newInvalidNewObjectException(typeName);
        }
        ITypedData typedData = dataManager.newData(type,
                DfIdUtil.getId(objectId), DfId.DF_NULLID);
        return buildObject(session, typedData, true, true);
    }

    public static boolean isAttrChanged(final IDfPersistentObject object,
            final String... attrNames) throws DfException {
        if (object.isNew()) {
            return true;
        }
        return isAttrChanged(object, getUnCached(object), attrNames);
    }

    public static boolean isAttrChanged(final IDfPersistentObject current,
            final IDfPersistentObject unCached, final String attrName)
        throws DfException {
        if (!current.hasAttr(attrName)) {
            return false;
        }
        if (unCached == null) {
            return true;
        }
        if (!current.getAttr(current.findAttrIndex(attrName)).isRepeating()) {
            return !Objects.equals(current.getValue(attrName),
                    unCached.getValue(attrName));
        }
        if (current.getValueCount(attrName) != unCached.getValueCount(attrName)) {
            return true;
        }
        for (int i = 0, n = current.getValueCount(attrName); i < n; i++) {
            if (!Objects.equals(current.getRepeatingValue(attrName, i),
                    unCached.getRepeatingValue(attrName, i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAttrChanged(final IDfPersistentObject current,
            final IDfPersistentObject unCached, final String... attrNames)
        throws DfException {
        if (attrNames == null || attrNames.length == 0) {
            return false;
        }
        for (String attrName : attrNames) {
            if (isAttrChanged(current, unCached, attrName)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IDfPersistentObject> T getUnCached(
            final IDfSession session, final IDfId objectId) throws DfException {
        return (T) ((ISession) session).getUncachedObject(objectId, null);
    }

    public static <T extends IDfPersistentObject> T getUnCached(final T object)
        throws DfException {
        return getUnCached(object.getObjectSession(), object.getObjectId());
    }

    public static boolean hasBlankAcl(final IDfSysObject object)
        throws DfException {
        return StringUtils.isBlank(object.getACLName())
                && StringUtils.isBlank(object.getACLDomain());
    }

    public static boolean isAclModified(final IDfSysObject object)
        throws DfException {
        return isAclModified(object, getUnCached(object));
    }

    public static boolean isAclModified(final IDfSysObject object,
            final IDfSysObject unCached) throws DfException {
        return isAttrChanged(object, unCached, DfDocbaseConstants.ACL_NAME,
                DfDocbaseConstants.ACL_DOMAIN);
    }

    public static boolean isFolderChanged(final IDfSysObject current)
        throws DfException {
        if (current.isNew()) {
            return true;
        }
        return isFolderChanged(current, getUnCached(current));
    }

    public static boolean isFolderChanged(final IDfSysObject current,
            final IDfSysObject uncached) throws DfException {
        if (uncached == null) {
            return true;
        }

        ISysObjectInternal internal = (ISysObjectInternal) current;
        int currentCount = internal.getFolderIdCountEx();
        int oldCount = ((ISysObjectInternal) uncached).getFolderIdCountEx();

        if (currentCount != oldCount) {
            return true;
        }

        for (int i = currentCount - 1; i >= 0; i--) {
            IDfId folderId = internal.getFolderIdEx(i);
            if (uncached.findId(DfDocbaseConstants.I_FOLDER_ID, folderId) == -1) {
                return true;
            }
        }
        return false;
    }

    public static void unlinkFromAllFolders(final IDfSysObject object)
        throws DfException {
        ISysObjectInternal internal = (ISysObjectInternal) object;
        for (int i = internal.getFolderIdCountEx() - 1; i >= 0; i--) {
            internal.unlink(internal.getFolderIdEx(i).getId());
        }
    }

    public static void removeVersionLabels(final IDfSysObject object)
        throws DfException {
        for (int i = object.getVersionLabelCount() - 1; i >= 0; i--) {
            object.unmark(object.getVersionLabel(i));
        }
    }

    public static void setVersionLabels(final IDfSysObject object,
            final Collection<String> labels) throws DfException {
        removeVersionLabels(object);
        for (String label : labels) {
            object.mark(label);
        }
    }

    public static int getValueCount(final IDfTypedObject object,
            final String attr) throws DfException {
        if (!object.hasAttr(attr)) {
            return 0;
        }
        return object.getValueCount(attr);
    }

    public static int getValueCount(final IDfTypedObject object,
            final String... attr) throws DfException {
        int result = getValueCount(object, attr[0]);
        for (int i = 1, n = attr.length; i < n; i++) {
            result = Math.min(result, getValueCount(object, attr[i]));
        }
        return result;
    }

    public static boolean isLinkedToFolder(final IDfSysObject object,
            final IDfId folderId) throws DfException {
        ISysObjectInternal internal = (ISysObjectInternal) object;
        for (int i = internal.getFolderIdCountEx() - 1; i >= 0; i--) {
            if (Objects.equals(internal.getFolderIdEx(i), folderId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLinkedToFolder(final IDfSysObject object,
            final String path) throws DfException {
        if (DfIdUtil.isCabinetOrFolderId(path)) {
            return isLinkedToFolder(object, DfIdUtil.getId(path));
        }
        if (DfIdUtil.isNullId(path)) {
            return false;
        }
        IDfSession session = object.getObjectSession();
        IDfFolder folder = session.getFolderByPath(path);
        return isLinkedToFolder(object, folder.getObjectId());
    }

    public static void unlinkAllParents(final IDfContent content)
        throws DfException {
        ((IContent) content).resetLinks();
    }

    public static void link(final IDfContent content, final IDfId objectId,
            final int pageNo, final String pageModifier) throws DfException {
        IDfSession session = content.getObjectSession();
        ((IContent) content).link((ISysObject) session.getObject(objectId),
                pageNo, pageModifier);
    }

    public static void link(final IDfContent content,
            final IDfSysObject object, final int pageNo,
            final String pageModifier) throws DfException {
        ((IContent) content).link((ISysObject) object, pageNo, pageModifier);
    }

    public static void setPrimary(final IDfSysObject dfObject,
            final IDfContent content) throws DfException {
        ISysObject object = (ISysObject) dfObject;
        object.setContentsId(content.getObjectId());
        if (object.getPageCount() == 0) {
            object.setPageCount(1);
        }
        object.setContentType(content.getFullFormat());
    }

    public static void resetAcl(final IDfACL acl) throws DfException {
        IDfList permissions = acl.getPermissions();
        for (int i = 0, n = permissions.getCount(); i < n; i++) {
            IDfPermit permit = (IDfPermit) permissions.get(i);
            acl.revokePermit(permit);
        }
        for (String accessor : new String[] {"dm_world", "dm_owner", }) {
            for (IDfPermit permit : PermitConverter.createDefault(accessor)) {
                acl.grantPermit(permit);
            }
        }
    }

    public static void setValue(final IDfPersistentObject object,
            final String attrName, final Object value, final int dataType,
            final int index) throws DfException {
        switch (dataType) {
        case IDfAttr.DM_BOOLEAN:
            object.setRepeatingBoolean(attrName, index, (Boolean) value);
            break;
        case IDfAttr.DM_DOUBLE:
            object.setRepeatingDouble(attrName, index, (Double) value);
            break;
        case IDfAttr.DM_INTEGER:
            object.setRepeatingInt(attrName, index, (Integer) value);
            break;
        case IDfAttr.DM_STRING:
            object.setRepeatingString(attrName, index, (String) value);
            break;
        case IDfAttr.DM_ID:
            object.setRepeatingId(attrName, index, (IDfId) value);
            break;
        case IDfAttr.DM_TIME:
            object.setRepeatingTime(attrName, index, (IDfTime) value);
            break;
        default:
            throw new IllegalArgumentException("Invalid datatype: " + dataType);
        }
    }

    public static void setValue(final IDfPersistentObject object,
            final String attrName, final IDfValue value, final int index)
        throws DfException {
        setValue(object, attrName, value, value.getDataType(), index);
    }

    public static void setValue(final IDfPersistentObject object,
            final String attrName, final IDfValue value, final int dataType,
            final int index) throws DfException {
        switch (dataType) {
        case IDfAttr.DM_BOOLEAN:
            object.setRepeatingBoolean(attrName, index, value.asBoolean());
            break;
        case IDfAttr.DM_DOUBLE:
            object.setRepeatingDouble(attrName, index, value.asDouble());
            break;
        case IDfAttr.DM_INTEGER:
            object.setRepeatingInt(attrName, index, value.asInteger());
            break;
        case IDfAttr.DM_STRING:
            object.setRepeatingString(attrName, index, value.asString());
            break;
        case IDfAttr.DM_ID:
            object.setRepeatingId(attrName, index, value.asId());
            break;
        case IDfAttr.DM_TIME:
            object.setRepeatingTime(attrName, index, value.asTime());
            break;
        default:
            throw new IllegalArgumentException("Invalid datatype: " + dataType);
        }
    }

    public static void setValue(final ITypedData object, final String attrName,
            final Object value, final int dataType, final int index)
        throws DfException {
        switch (dataType) {
        case IDfAttr.DM_BOOLEAN:
            object.setRepeatingBoolean(attrName, index, (Boolean) value);
            break;
        case IDfAttr.DM_DOUBLE:
            object.setRepeatingDouble(attrName, index, (Double) value);
            break;
        case IDfAttr.DM_INTEGER:
            object.setRepeatingInt(attrName, index, (Integer) value);
            break;
        case IDfAttr.DM_STRING:
            object.setRepeatingString(attrName, index, (String) value);
            break;
        case IDfAttr.DM_ID:
            object.setRepeatingId(attrName, index, (IDfId) value);
            break;
        case IDfAttr.DM_TIME:
            object.setRepeatingTime(attrName, index, (IDfTime) value);
            break;
        default:
            throw new IllegalArgumentException("Invalid datatype: " + dataType);
        }
    }

    public static void setValue(final ITypedData object, final String attrName,
            final IDfValue value, final int index) throws DfException {
        setValue(object, attrName, value, value.getDataType(), index);
    }

    public static void setValue(final ITypedData object, final String attrName,
            final IDfValue value, final int dataType, final int index)
        throws DfException {
        switch (dataType) {
        case IDfAttr.DM_BOOLEAN:
            object.setRepeatingBoolean(attrName, index, value.asBoolean());
            break;
        case IDfAttr.DM_DOUBLE:
            object.setRepeatingDouble(attrName, index, value.asDouble());
            break;
        case IDfAttr.DM_INTEGER:
            object.setRepeatingInt(attrName, index, value.asInteger());
            break;
        case IDfAttr.DM_STRING:
            object.setRepeatingString(attrName, index, value.asString());
            break;
        case IDfAttr.DM_ID:
            object.setRepeatingId(attrName, index, value.asId());
            break;
        case IDfAttr.DM_TIME:
            object.setRepeatingTime(attrName, index, value.asTime());
            break;
        default:
            throw new IllegalArgumentException("Invalid datatype: " + dataType);
        }
    }

    public static Object getValue(final ITypedData object,
            final String attrName, final int dataType, final int index)
        throws DfException {
        switch (dataType) {
        case IDfAttr.DM_BOOLEAN:
            return object.getRepeatingBoolean(attrName, index);
        case IDfAttr.DM_DOUBLE:
            return object.getRepeatingDouble(attrName, index);
        case IDfAttr.DM_INTEGER:
            return object.getRepeatingInt(attrName, index);
        case IDfAttr.DM_STRING:
            return object.getRepeatingString(attrName, index);
        case IDfAttr.DM_ID:
            return object.getRepeatingId(attrName, index);
        case IDfAttr.DM_TIME:
            return object.getRepeatingTime(attrName, index);
        default:
            throw new IllegalArgumentException("Invalid datatype: " + dataType);
        }
    }

    public static void markDeleted(final IDfSysObject object,
            final boolean deleted) throws DfException {
        markDeleted(((ISysObject) object).getData(true), deleted);
    }

    public static void markDeleted(final ITypedData object,
            final boolean deleted) throws DfException {
        setValue(object, "i_is_deleted", deleted, IDfAttr.DM_BOOLEAN, 0);
    }

}
