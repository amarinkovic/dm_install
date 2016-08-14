package pro.documentum.persistence.common.connection;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.datanucleus.store.StoreManager;

import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfPreferences;

import pro.documentum.util.preferences.PreferencesLoader;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class SessionManagerPoolFactory {

    public static final String MAX_POOL_SIZE = "datanucleus.connectionPool.maxPoolSize";

    public static final String MAX_IDLE = "datanucleus.connectionPool.maxIdle";

    public static final String MIN_IDLE = "datanucleus.connectionPool.minIdle";

    public static final String MAX_LIFE_TIME = "datanucleus.connectionPool.maxLifetime";

    public static final String MAX_WAIT_TIME = "datanucleus.connectionPool.maxWaittime";

    private static final SessionManagerPoolFactory INSTANCE = new SessionManagerPoolFactory();

    private SessionManagerPoolFactory() {
        super();
    }

    static SessionManagerPoolFactory getInstance() {
        return INSTANCE;
    }

    ObjectPool<IDfSessionManager> createSessionManagerPool(
            final StoreManager storeManager) {
        loadDfcPreferences(storeManager);
        return new GenericObjectPool<>(new SessionManagerFactory(),
                createPoolConfig(storeManager));
    }

    private void loadDfcPreferences(final StoreManager storeManager) {
        Map<String, String> preferences = extractPreferences(storeManager);
        extrapolatePreferences(preferences);
        createConfigDirectory(preferences);
        PreferencesLoader.load(preferences, true);
    }

    private Map<String, String> extractPreferences(
            final StoreManager storeManager) {
        Map<String, String> preferences = new HashMap<>();
        List<String> knownPreferences = PreferencesLoader.getKnownPreferences();
        for (String preferenceName : knownPreferences) {
            if (!storeManager.hasProperty(preferenceName)) {
                continue;
            }
            String value = storeManager.getStringProperty(preferenceName);
            if (value == null) {
                continue;
            }
            preferences.put(preferenceName, value);
        }
        return preferences;
    }

    private void extrapolatePreferences(final Map<String, String> preferences) {
        for (Map.Entry<String, String> entry : preferences.entrySet()) {
            String value = entry.getValue();
            value = PreferencesLoader.replaceProperties(value, preferences);
            entry.setValue(value);
        }
    }

    private void createConfigDirectory(final Map<String, String> preferences) {
        final String configDir = preferences.get(DfPreferences.DFC_CONFIG_DIR);
        if (StringUtils.isBlank(configDir)) {
            return;
        }
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                File directory = new File(configDir);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                return null;
            }
        });
    }

    private GenericObjectPoolConfig createPoolConfig(
            final StoreManager storeManager) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        setMaxTotal(config, storeManager);
        setMaxIdle(config, storeManager);
        setMinIdle(config, storeManager);
        setMaxLifeTime(config, storeManager);
        setMaxWaitTime(config, storeManager);
        return config;
    }

    private void setMaxWaitTime(final GenericObjectPoolConfig config,
            final StoreManager storeManager) {
        int waitTime = storeManager.getIntProperty(MAX_WAIT_TIME);
        if (waitTime > 0) {
            config.setMaxWaitMillis(waitTime * 1000);
        }
    }

    private void setMaxLifeTime(final GenericObjectPoolConfig config,
            final StoreManager storeManager) {
        int lifeTime = storeManager.getIntProperty(MAX_LIFE_TIME);
        if (lifeTime > 0) {
            config.setMinEvictableIdleTimeMillis(lifeTime * 1000);
        }
    }

    private void setMinIdle(final GenericObjectPoolConfig config,
            final StoreManager storeManager) {
        int minIdle = storeManager.getIntProperty(MIN_IDLE);
        if (minIdle > 0) {
            config.setMinIdle(minIdle);
        }
    }

    private void setMaxIdle(final GenericObjectPoolConfig config,
            final StoreManager storeManager) {
        int maxIdle = storeManager.getIntProperty(MAX_IDLE);
        if (maxIdle > 0) {
            config.setMaxIdle(maxIdle);
        }
    }

    private void setMaxTotal(final GenericObjectPoolConfig config,
            final StoreManager storeManager) {
        int maxPoolSize = storeManager.getIntProperty(MAX_POOL_SIZE);
        if (maxPoolSize > 0) {
            config.setMaxTotal(maxPoolSize);
        }
    }

}
