package pro.documentum.util.objects.changes.attributes.user;

import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;

import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class UserNameHandler extends AbstractUserAttributeHandler {

    public UserNameHandler() {
        super();
    }

    @Override
    protected boolean doAccept(final Set<String> attrNames) {
        return attrNames.contains("user_name");
    }

    @Override
    protected boolean doApply(final IDfUser user, final Map<String, ?> values)
        throws DfException {
        String userName = (String) values.remove("user_name");
        Logger.debug("Setting {0} value of object {1} to {2}", "user_name",
                user.getObjectId(), userName);
        user.setUserName(userName);
        return true;
    }

}
