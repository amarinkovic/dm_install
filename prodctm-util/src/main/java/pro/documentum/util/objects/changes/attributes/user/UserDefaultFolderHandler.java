package pro.documentum.util.objects.changes.attributes.user;

import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;

import pro.documentum.util.logger.Logger;
import pro.documentum.util.objects.changes.attributes.Depends;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Depends(on = {UserNameHandler.class })
public class UserDefaultFolderHandler extends AbstractUserAttributeHandler {

    public UserDefaultFolderHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return attrNames.contains("default_folder");
    }

    @Override
    protected boolean doApply(final IDfUser user, final Map<String, ?> values)
        throws DfException {
        String folder = (String) values.remove("default_folder");
        if (folder != null) {
            Logger.debug("Setting {0} value of object {1} to {2}",
                    "default_folder", user.getObjectId(), folder);
            user.setDefaultFolder(folder, true);
        }
        return true;
    }

}
