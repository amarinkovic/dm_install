package pro.documentum.util.versions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.impl.IPersistentObject;
import com.documentum.fc.client.impl.ISysObject;
import com.documentum.fc.client.impl.typeddata.ITypedData;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfValue;

import pro.documentum.aspects.DfTransactional;
import pro.documentum.util.objects.DfObjects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class VersionTreeCopier {

    public static final String[] VERSION_ATTRS = new String[] {
        DfDocbaseConstants.I_CHRONICLE_ID, "i_antecedent_id", "i_has_folder",
        "i_latest_flag", DfDocbaseConstants.R_VERSION_LABEL, "i_branch_cnt",
        "i_direct_dsc", };

    private VersionTreeCopier() {
        super();
    }

    @DfTransactional
    public static Map<IDfId, IDfId> copy(final IDfSession session,
            final IDfId objectId, final boolean shareContent,
            final boolean copyRelations,
            final boolean keepStorageAreasForRenditions) throws DfException {
        IDfSysObject object = (IDfSysObject) session.getObject(objectId);
        Map<IDfId, IDfId> old2new = new HashMap<>();
        List<IDfId> deleted = new ArrayList<>();
        List<IDfSysObject> oldDocs = Versions.getDocumentsTree(object);
        List<IDfSysObject> newDocs = new ArrayList<>();
        for (IDfSysObject doc : oldDocs) {
            boolean isDirty = doc.isDirty();
            boolean isDeleted = doc.isDeleted();
            try {
                if (isDeleted) {
                    deleted.add(doc.getObjectId());
                    DfObjects.markDeleted(doc, false);
                }
                IDfId copyId = ((ISysObject) doc).saveAsNew(shareContent,
                        copyRelations, keepStorageAreasForRenditions, session);
                old2new.put(doc.getObjectId(), copyId);
                newDocs.add((IDfSysObject) session.getObject(copyId));
            } finally {
                if (isDeleted) {
                    if (isDirty) {
                        DfObjects.markDeleted(doc, true);
                    } else {
                        doc.revert();
                    }
                }
            }
        }

        for (int i = 0, n = newDocs.size(); i < n; i++) {
            ISysObject copy = (ISysObject) newDocs.get(i);
            IDfSysObject old = oldDocs.get(i);
            List<String> attrs = new ArrayList<>();
            Collections.addAll(attrs, VERSION_ATTRS);
            for (int j = 0, m = copy.getAttrCount(); j < m; j++) {
                IDfAttr attr = copy.getAttr(j);
                if (DfDocbaseConstants.R_OBJECT_ID.equals(attr.getName())) {
                    continue;
                }
                if (IDfAttr.DM_ID != attr.getDataType()) {
                    continue;
                }
                if (attrs.contains(attr.getName())) {
                    continue;
                }
                attrs.add(attr.getName());
            }
            copyAttributes(old, copy, old2new, attrs);
            copy.save();
        }

        for (IDfId deletedId : deleted) {
            IDfId newId = old2new.get(deletedId);
            session.getObject(newId).destroy();
        }

        return old2new;
    }

    private static void copyAttributes(final IDfPersistentObject from,
            final IPersistentObject to, final Map<IDfId, IDfId> old2new,
            final List<String> attrNames) throws DfException {
        if (attrNames == null || attrNames.isEmpty()) {
            return;
        }
        for (String attrName : attrNames) {
            copyAttributeInternal(from, to, old2new, attrName);
        }
    }

    private static void copyAttributeInternal(final IDfPersistentObject from,
            final IPersistentObject to, final Map<IDfId, IDfId> old2new,
            final String attrName) throws DfException {
        if (!from.hasAttr(attrName) || !to.hasAttr(attrName)) {
            return;
        }
        if (from.isAttrRepeating(attrName) != to.isAttrRepeating(attrName)) {
            throw new DfException("Unable to copy attribute " + attrName
                    + ", repeating mismatch");
        }
        if (from.getAttrDataType(attrName) != to.getAttrDataType(attrName)) {
            throw new DfException("Unable to copy attribute " + attrName
                    + ", type mismatch");
        }
        copyAttributeInternal(from, to.getData(true), old2new, attrName);
    }

    private static void copyAttributeInternal(final IDfPersistentObject from,
            final ITypedData to, final Map<IDfId, IDfId> old2new,
            final String attrName) throws DfException {
        int end = 0;
        if (from.isAttrRepeating(attrName)) {
            to.removeAll(attrName);
            end = from.getValueCount(attrName);
        }
        for (int i = 0; i < end; i++) {
            IDfValue value = from.getRepeatingValue(attrName, i);
            value = convert(value, old2new);
            DfObjects.setValue(to, attrName, value, i);
        }
    }

    private static IDfValue convert(final IDfValue oldValue,
            final Map<IDfId, IDfId> old2new) throws DfException {
        if (old2new == null) {
            return oldValue;
        }
        if (!(oldValue.getDataType() == IDfAttr.DM_ID)) {
            return oldValue;
        }
        IDfId id = oldValue.asId();
        if (old2new.containsKey(id)) {
            id = old2new.get(id);
            return new DfValue(id, IDfAttr.DM_ID);
        }
        return oldValue;
    }

}
