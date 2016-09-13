package pro.documentum.ext.isis;

import java.util.List;

import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.runtime.authentication.AuthenticationManagerStandardInstallerAbstractForDfltRuntime;
import org.apache.isis.core.runtime.authentication.standard.Authenticator;

import com.google.common.collect.Lists;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AuthenticationManagerInstaller extends
        AuthenticationManagerStandardInstallerAbstractForDfltRuntime {

    public static final String NAME = "documentum";

    public AuthenticationManagerInstaller() {
        super(NAME);
    }

    @Override
    protected List<Authenticator> createAuthenticators(
            final IsisConfiguration configuration) {
        return Lists
                .<Authenticator> newArrayList(new AuthenticatorOrAuthorizor(
                        configuration));
    }

}
