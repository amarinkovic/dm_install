package pro.documentum.persistence.common;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface ICredentialsHolder {

    String OPTION_LOGININFO = "loginInfo";

    String getUserName();

    String getPassword();

}
