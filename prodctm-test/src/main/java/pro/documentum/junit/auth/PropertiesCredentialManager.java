package pro.documentum.junit.auth;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.impl.security.action.GetPropertyAction;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PropertiesCredentialManager extends AbstractCredentialManager {

    private static final String DOCBASE = "docbase";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    private static final String DOMAIN = "domain";

    private static final String PROPERTIES_FILE = "documentumcredentials.properties";

    private static final String PROPERTIES_FILE_LOCATION = "documentumcredentials.location";

    public PropertiesCredentialManager(final String docbaseName) {
        super(docbaseName);
    }

    @Override
    public IDocumentumCredentials getCredentials(final String userName,
            final String password) {
        return doGetCredentials();
    }

    private IDocumentumCredentials doGetCredentials() {
        return getData(getProperties());
    }

    @Override
    public String getDocbaseName() {
        return doGetCredentials().getDocbaseName();
    }

    private IDocumentumCredentials getData(final Properties properties) {
        if (properties == null) {
            throw new NullPointerException("Empty properties");
        }
        String docbase = properties.getProperty(DOCBASE);
        if (docbase == null) {
            throw new NullPointerException("Empty docbaseName");
        }
        String userName = properties.getProperty(USERNAME);
        if (userName == null) {
            throw new NullPointerException("Empty userName");
        }
        String password = properties.getProperty(PASSWORD);
        String domain = properties.getProperty(DOMAIN);
        return new DocumentumCredentials(docbase, userName, password, domain);
    }

    private Properties getProperties() {
        Properties properties = loadFromLocation();
        if (properties == null) {
            properties = loadFromClassPath();
        }
        return properties;
    }

    private Properties loadFromClassPath() {
        InputStream stream = getResourceAsStream(PROPERTIES_FILE);
        if (stream != null) {
            return load(stream);
        }
        return null;
    }

    private InputStream getResourceAsStream(final String name) {
        return getResourceAsStream(getClass(), name);
    }

    public static InputStream getResourceAsStream(final Class<?> clazz,
            final String name) {
        InputStream stream = clazz.getResourceAsStream(name);
        if (stream != null) {
            return stream;
        }

        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(name);
        }
        if (stream != null) {
            return stream;
        }

        classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(name);
        }
        if (stream != null) {
            return stream;
        }

        stream = ClassLoader.getSystemResourceAsStream(name);
        if (stream != null) {
            return stream;
        }
        return null;
    }

    private Properties loadFromLocation() {
        String propertiesLocation = getPropertiesLocation();
        if (propertiesLocation == null) {
            return null;
        }
        try {
            URL location = new URL(propertiesLocation);
            return load(location.openStream());
        } catch (MalformedURLException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    private String getPropertiesLocation() {
        return AccessController.doPrivileged(new GetPropertyAction(
                PROPERTIES_FILE_LOCATION));
    }

    private Properties load(final InputStream stream) {
        try {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        } catch (IOException ex) {
            return null;
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

}
