package pro.documentum.util.auth;

import java.util.Objects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class BaseCredentials implements ICredentials {

    private String _userName;

    private String _password;

    public BaseCredentials() {
        super();
    }

    @Override
    public String getUserName() {
        return _userName;
    }

    @Override
    public void setUserName(final String userName) {
        _userName = userName;
    }

    @Override
    public String getPassword() {
        return _password;
    }

    @Override
    public void setPassword(final String password) {
        _password = password;
    }

    @Override
    public String getDocbase() {
        return null;
    }

    @Override
    public void setDocbase(final String docbase) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sync(final ICredentials other) {
        setUserName(other.getUserName());
        setPassword(other.getPassword());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ICredentials)) {
            return false;
        }
        ICredentials that = (ICredentials) o;
        return Objects.equals(_userName, that.getUserName())
                && Objects.equals(_password, that.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(_userName, _password);
    }

}
