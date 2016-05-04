package pro.documentum.junit.auth;

import java.util.Objects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DocumentumCredentials implements IDocumentumCredentials {

	private final String _docbaseName;

	private final String _userName;

	private final String _password;

	private final String _domain;

	public DocumentumCredentials(String docbaseName, String userName, String password, String domain) {
		_docbaseName = docbaseName;
		_userName = userName;
		_password = password;
		_domain = domain;
	}

	public String getDocbaseName() {
		return _docbaseName;
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
	public String getDomain() {
		return _domain;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DocumentumCredentials that = (DocumentumCredentials) o;
		return Objects.equals(_docbaseName, that._docbaseName) && Objects.equals(_userName, that._userName)
				&& Objects.equals(_password, that._password) && Objects.equals(_domain, that._domain);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_docbaseName, _userName, _password, _domain);
	}

}
