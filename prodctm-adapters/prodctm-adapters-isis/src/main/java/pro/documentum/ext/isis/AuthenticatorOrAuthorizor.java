package pro.documentum.ext.isis;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import org.apache.isis.applib.Identifier;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.runtime.authentication.AuthenticationRequest;
import org.apache.isis.core.runtime.authentication.AuthenticationRequestPassword;
import org.apache.isis.core.runtime.authentication.standard.Authenticator;
import org.apache.isis.core.runtime.authorization.standard.Authorizor;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;
import org.apache.isis.core.runtime.system.session.IsisSession;

import com.documentum.fc.client.DfAuthenticationException;

import pro.documentum.util.java.Exceptions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AuthenticatorOrAuthorizor implements Authenticator, Authorizor {

    private final IsisConfiguration _configuration;

    public AuthenticatorOrAuthorizor(final IsisConfiguration configuration) {
        _configuration = configuration;
    }

    @Override
    public void init() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public final boolean canAuthenticate(
            final Class<? extends AuthenticationRequest> authenticationRequestClass) {
        return AuthenticationRequestPassword.class
                .isAssignableFrom(authenticationRequestClass);
    }

    @Override
    public AuthenticationSession authenticate(
            final AuthenticationRequest request, final String code) {
        AuthenticationRequestPassword passwordRequest = (AuthenticationRequestPassword) request;
        String username = passwordRequest.getName();
        String password = passwordRequest.getPassword();
        AuthenticationSession authSession = new AuthenticationSession(username,
                password);
        IsisSession isisSession = null;
        try {
            isisSession = IsisContext.openSession(authSession);
            PersistenceSession ps = isisSession.getPersistenceSession();
            authSession.addRoles(getRoles(ps));
        } catch (Throwable t) {
            if (Exceptions.inStack(t, DfAuthenticationException.class)) {
                return null;
            }
            throw t;
        } finally {
            if (isisSession != null) {
                isisSession.closeAll();
            }
        }
        authSession.addRoles(request.getRoles());
        return authSession;
    }

    protected List<String> getRoles(final PersistenceSession ps) {
        PersistenceManager pm = ps.getPersistenceManager();
        JDOConnection connection = null;
        try {
            connection = pm.getDataStoreConnection();
            return new ArrayList<>();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public boolean isValid(AuthenticationRequest request) {
        return false;
    }

    @Override
    public boolean isVisibleInAnyRole(Identifier identifier) {
        return isPermitted(identifier, "r");
    }

    @Override
    public boolean isUsableInAnyRole(Identifier identifier) {
        return isPermitted(identifier, "w");
    }

    private boolean isPermitted(Identifier identifier, String qualifier) {
        return true;
    }

    @Override
    public boolean isVisibleInRole(String role, Identifier identifier) {
        return false;
    }

    @Override
    public boolean isUsableInRole(String role, Identifier identifier) {
        return false;
    }

    public IsisConfiguration getConfiguration() {
        return _configuration;
    }

}
