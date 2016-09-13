package pro.documentum.util.auth;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface ICredentials {

    String getDocbase();

    String getUserName();

    String getPassword();

    void setDocbase(String docbase);

    void setUserName(String userName);

    void setPassword(String password);

    void sync(ICredentials other);

}
