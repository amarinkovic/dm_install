package pro.documentum.ext.isis;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.isis.core.commons.authentication.MessageBroker;
import org.apache.isis.core.commons.encoding.DataOutputExtended;

import pro.documentum.util.auth.ICredentials;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AuthenticationSession implements ICredentials,
        org.apache.isis.core.commons.authentication.AuthenticationSession,
        Serializable {

    private final String _userName;

    private final String _password;

    private final List<String> _roles;

    private final Map<String, Object> _attributes;

    private MessageBroker _messageBroker;

    public AuthenticationSession(String userName, String password) {
        _userName = userName;
        _password = password;
        _roles = new ArrayList<>();
        _attributes = new HashMap<>();
    }

    @Override
    public String getDocbase() {
        return null;
    }

    @Override
    public String getUserName() {
        return _userName;
    }

    @Override
    public String getPassword() {
        return _password;
    }

    @Override
    public void setDocbase(String docbase) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserName(String userName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPassword(String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sync(ICredentials other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(DataOutputExtended dataOutputExtended)
        throws IOException {

    }

    @Override
    public boolean hasUserNameOf(String userName) {
        if (userName == null) {
            return false;
        }
        return userName.equals(getUserName());
    }

    void addRole(String roleName) {
        _roles.add(roleName);
    }

    void addRoles(Collection<String> roleNames) {
        _roles.addAll(roleNames);
    }

    @Override
    public List<String> getRoles() {
        return Collections.unmodifiableList(_roles);
    }

    @Override
    public String getValidationCode() {
        return null;
    }

    @Override
    public Object getAttribute(String attribute) {
        return _attributes.get(attribute);
    }

    @Override
    public void setAttribute(String attribute, Object value) {
        _attributes.put(attribute, value);
    }

    @Override
    public MessageBroker getMessageBroker() {
        return _messageBroker;
    }

    @Override
    public void setMessageBroker(MessageBroker messageBroker) {
        _messageBroker = messageBroker;
    }

}
