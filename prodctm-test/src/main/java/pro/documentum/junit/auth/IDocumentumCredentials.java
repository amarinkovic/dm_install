package pro.documentum.junit.auth;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IDocumentumCredentials {

	String getDocbaseName();

	String getUserName();

	String getPassword();

	String getDomain();

}
