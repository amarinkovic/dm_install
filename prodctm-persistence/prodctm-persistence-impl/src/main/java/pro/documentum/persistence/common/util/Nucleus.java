package pro.documentum.persistence.common.util;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.Configuration;
import org.datanucleus.ExecutionContext;
import org.datanucleus.PropertyNames;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.identity.IdentityManager;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.metadata.AbstractClassMetaData;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import pro.documentum.util.auth.ICredentials;
import pro.documentum.util.ids.DfIdUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Nucleus {

    public static final String LOGIN_PROPERTY = PropertyNames.PROPERTY_CONNECTION_USER_NAME;

    public static final String PASSWORD_PROPERTY = PropertyNames.PROPERTY_CONNECTION_PASSWORD;

    private Nucleus() {
        super();
    }

    public static boolean hasTargetClass(final Object id) {
        String targetClass = IdentityUtils
                .getTargetClassNameForIdentitySimple(id);
        return StringUtils.isNotBlank(targetClass);
    }

    public static String getTargetClass(final Object id) {
        return IdentityUtils.getTargetClassNameForIdentitySimple(id);
    }

    public static Object getIdentity(final ExecutionContext context,
            final AbstractClassMetaData cmd, final IDfTypedObject object) {
        IdentityManager im = context.getNucleusContext().getIdentityManager();
        return im.getDatastoreId(cmd.getFullClassName(),
                DNValues.getObjectId(object));
    }

    public static IDfLoginInfo extractLoginInfo(final ExecutionContext ec) {
        if (ec == null) {
            return null;
        }
        Object owner = ec.getOwner();
        IDfLoginInfo loginInfo = null;
        if (owner instanceof ICredentials) {
            loginInfo = getLoginInfo((ICredentials) owner);
        }
        if (loginInfo == null) {
            loginInfo = getLoginInfo(ec);
        }
        if (loginInfo == null) {
            loginInfo = getLoginInfo(ec.getNucleusContext().getConfiguration());
        }
        return loginInfo;
    }

    private static IDfLoginInfo getLoginInfo(
            final ICredentials credentialsHolder) {
        String userName = credentialsHolder.getUserName();
        String password = credentialsHolder.getPassword();
        if (userName != null) {
            return new DfLoginInfo(userName, password);
        }
        return null;
    }

    private static IDfLoginInfo getLoginInfo(final Configuration cnf) {
        String userName = cnf.getStringProperty(LOGIN_PROPERTY);
        String password = cnf.getStringProperty(PASSWORD_PROPERTY);
        if (userName != null) {
            return new DfLoginInfo(userName, password);
        }
        return null;
    }

    private static IDfLoginInfo getLoginInfo(final ExecutionContext ec) {
        String userName = ec.getStringProperty(LOGIN_PROPERTY);
        String password = ec.getStringProperty(PASSWORD_PROPERTY);
        if (userName != null) {
            return new DfLoginInfo(userName, password);
        }
        return null;
    }

    public static String getDocumentumId(final Object id) {
        String objectId = null;
        if (IdentityUtils.isDatastoreIdentity(id)) {
            objectId = (String) IdentityUtils
                    .getTargetKeyForDatastoreIdentity(id);
        } else if (IdentityUtils.isSingleFieldIdentity(id)) {
            objectId = (String) IdentityUtils
                    .getTargetKeyForSingleFieldIdentity(id);
        }
        if (DfIdUtil.isNotObjectId(objectId)) {
            throw new NucleusObjectNotFoundException("Invalid objectId: "
                    + objectId);
        }
        return objectId;
    }

}
