package pro.documentum.configservice;

import com.documentum.services.config.IConfigService;

import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
final class ConfigServiceFactory {

    public static final String CONFIG_SERVICE_CLASS_NAME = "com.documentum.services.config.impl.ConfigService";

    private static final Class<? extends IConfigService> CONFIG_SERVICE_CLASS;

    static {
        try {
            CONFIG_SERVICE_CLASS = initConfigServiceClass();
        } catch (ClassNotFoundException ex) {
            Logger.error(ex);
            Error error = new NoClassDefFoundError(CONFIG_SERVICE_CLASS_NAME);
            error.initCause(ex);
            throw error;
        }
    }

    private final IConfigProvider _configProvider;

    private Long _lastRefreshChecksum;

    private long _lastRefreshTime;

    private ConfigServiceFactory(final IConfigProvider configProvider) {
        _configProvider = configProvider;
    }

    public static ConfigServiceFactory getInstance(
            final IConfigProvider configProvider) {
        return new ConfigServiceFactory(configProvider);
    }

    private boolean needRefresh() {
        return System.currentTimeMillis() > _lastRefreshTime
                + _configProvider.getRefreshInterval();
    }

    public IConfigService getConfigService() {
        try {

            if (!needRefresh()) {
                return null;
            }

            IConfigService configService = instantiate();
            DocbaseConfigReader reader = DocbaseConfigReader
                    .getInstance(_configProvider);

            Long checksum = reader.getCurrentChecksum();

            // unable to find configs, staying with the old ones
            if (checksum == null) {
                return null;
            }

            if (checksum.equals(_lastRefreshChecksum)) {
                // checksum equals the previous one,
                // just updating last refresh date
                _lastRefreshTime = System.currentTimeMillis();
                return null;
            }

            configService.loadConfig(reader,
                    DocbaseContext.getInstance(_configProvider));
            configService.setNlsReader(new NlsReader());
            _lastRefreshChecksum = checksum;
            _lastRefreshTime = System.currentTimeMillis();
            return configService;
        } catch (Exception ex) {
            Logger.error(ex);
        } catch (Error e) {
            Logger.error(e);
            throw e;
        }
        return null;
    }

    private static IConfigService instantiate() throws InstantiationException,
        IllegalAccessException {
        return CONFIG_SERVICE_CLASS.newInstance();
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends IConfigService> initConfigServiceClass()
        throws ClassNotFoundException {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = ConfigServiceFactory.class.getClassLoader();
        }
        ClassLoader classLoader = new ConfigServiceClassLoader(parent);
        return (Class<? extends IConfigService>) Class.forName(
                CONFIG_SERVICE_CLASS_NAME, true, classLoader);
    }

}
