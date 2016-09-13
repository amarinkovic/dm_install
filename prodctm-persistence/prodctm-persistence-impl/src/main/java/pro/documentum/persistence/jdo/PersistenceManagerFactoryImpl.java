package pro.documentum.persistence.jdo;

import java.util.Map;
import java.util.Properties;

import javax.jdo.Constants;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.metadata.PersistenceUnitMetaData;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistenceManagerFactoryImpl extends JDOPersistenceManagerFactory {

    private static final long serialVersionUID = 1947522885236246186L;

    private static final ThreadLocal<Boolean> INITIALIZING = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    public PersistenceManagerFactoryImpl() {
        super();
    }

    public PersistenceManagerFactoryImpl(final PersistenceUnitMetaData pumd,
            final Map<?, ?> overrideProps) {
        super(pumd, overrideProps);
    }

    public PersistenceManagerFactoryImpl(final Map<?, ?> props) {
        super(props);
    }

    public static synchronized PersistenceManagerFactory getPersistenceManagerFactory(
            final Properties overridingProps) {
        if (needDelegate(overridingProps)) {
            return JDOPersistenceManagerFactory
                    .getPersistenceManagerFactory(overridingProps);
        }
        String factoryName = getPersistentFactoryName(overridingProps);
        if (INITIALIZING.get() || factoryName == null) {
            return JDOPersistenceManagerFactory
                    .getPersistenceManagerFactory(overridingProps);
        }
        return doGetPersistenceManagerFactory(overridingProps);
    }

    public static synchronized PersistenceManagerFactory getPersistenceManagerFactory(
            final Map overridingProps) {
        if (needDelegate(overridingProps)) {
            return JDOPersistenceManagerFactory
                    .getPersistenceManagerFactory(overridingProps);
        }
        return doGetPersistenceManagerFactory(overridingProps);
    }

    public static synchronized PersistenceManagerFactory getPersistenceManagerFactory(
            final Map overrides, final Map props) {
        if (needDelegate(overrides, props)) {
            return JDOPersistenceManagerFactory.getPersistenceManagerFactory(
                    overrides, props);
        }
        return doGetPersistenceManagerFactory(overrides, props);
    }

    private static PersistenceManagerFactory doGetPersistenceManagerFactory(
            final Map<?, ?>... props) {
        try {
            INITIALIZING.set(true);
            String factoryName = getPersistentFactoryName(props);
            return JDOHelper.getPersistenceManagerFactory(factoryName);
        } finally {
            INITIALIZING.set(false);
        }
    }

    private static boolean needDelegate(final Map<?, ?>... props) {
        return INITIALIZING.get() || getPersistentFactoryName(props) == null;
    }

    private static String getPersistentFactoryName(final Map<?, ?>... props) {
        for (Map<?, ?> properties : props) {
            if (properties == null) {
                continue;
            }
            if (properties.containsKey(Constants.PROPERTY_NAME)) {
                return (String) properties.get(Constants.PROPERTY_NAME);
            }
        }
        return null;
    }

    @Override
    protected JDOPersistenceManager newPM(
            final JDOPersistenceManagerFactory jdoPmf, final String userName,
            final String password) {
        return new PersistenceManagerImpl(jdoPmf, userName, password);
    }

}
