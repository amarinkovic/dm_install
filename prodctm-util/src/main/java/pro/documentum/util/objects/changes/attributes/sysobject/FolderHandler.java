package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

import pro.documentum.util.logger.Logger;
import pro.documentum.util.objects.DfObjects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class FolderHandler extends AbstractSysObjectAttributeHandler {

    public static final Set<String> FOLDER_ATTRIBUTES;

    static {
        Set<String> folderAttributes = new HashSet<>();
        folderAttributes.add("i_folder_id");
        folderAttributes.add("r_folder_path");
        FOLDER_ATTRIBUTES = Collections.unmodifiableSet(folderAttributes);
    }

    public FolderHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return containsKey(attrNames, FOLDER_ATTRIBUTES);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean doApply(final IDfSysObject sysObject,
            final Map<String, ?> values) throws DfException {
        List<IDfId> newFolders = (List<IDfId>) values.remove("i_folder_id");
        DfObjects.unlinkFromAllFolders(sysObject);
        for (IDfId folderId : newFolders) {
            Logger.debug("Linking object {1} to folder {2}",
                    sysObject.getObjectId(), folderId);
            sysObject.link(folderId.getId());
        }
        return false;
    }

}
