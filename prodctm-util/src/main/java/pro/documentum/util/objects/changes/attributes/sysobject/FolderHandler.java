package pro.documentum.util.objects.changes.attributes.sysobject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

import pro.documentum.util.objects.DfObjects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class FolderHandler extends AbstractSysObjectAttributeHandler {

    private static final Set<String> FOLDER_ATTRIBUTES;

    static {
        FOLDER_ATTRIBUTES = new HashSet<String>();
        FOLDER_ATTRIBUTES.add("i_folder_id");
        FOLDER_ATTRIBUTES.add("r_folder_path");
    }

    public FolderHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        for (String attrName : FOLDER_ATTRIBUTES) {
            if (attrNames.contains(attrName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean doApply(final IDfSysObject object,
            final Map<String, ?> values) throws DfException {
        List<IDfId> newFolders = (List<IDfId>) values.remove("i_folder_id");
        DfObjects.unlinkFromAllFolders(object);
        for (IDfId folderId : newFolders) {
            object.link(folderId.getId());
        }
        return false;
    }

}
