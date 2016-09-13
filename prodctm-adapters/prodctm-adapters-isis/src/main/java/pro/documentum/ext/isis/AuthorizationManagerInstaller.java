package pro.documentum.ext.isis;

import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.runtime.authorization.standard.AuthorizationManagerStandardInstallerAbstract;
import org.apache.isis.core.runtime.authorization.standard.Authorizor;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AuthorizationManagerInstaller extends
        AuthorizationManagerStandardInstallerAbstract {

    public static final String NAME = "documentum";

    public AuthorizationManagerInstaller() {
        super(NAME);
    }

    @Override
    protected Authorizor createAuthorizor(IsisConfiguration configuration) {
        return new AuthenticatorOrAuthorizor(configuration);
    }

}
