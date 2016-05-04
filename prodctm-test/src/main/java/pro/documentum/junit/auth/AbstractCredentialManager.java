package pro.documentum.junit.auth;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractCredentialManager implements ICredentialManager {

	private final String _docbaseName;

	AbstractCredentialManager(String docbaseName) {
		_docbaseName = docbaseName;
	}

	@Override
	public String getDocbaseName() {
		return _docbaseName;
	}

}
