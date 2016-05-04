package pro.documentum.junit.auth;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface ICredentialManager {

	String getDocbaseName();

	IDocumentumCredentials getCredentials(String userName, String password);

}
