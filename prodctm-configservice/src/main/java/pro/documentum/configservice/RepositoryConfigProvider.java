package pro.documentum.configservice;

import java.util.Objects;

import com.documentum.fc.client.IDfSession;
import com.documentum.services.config.IConfigElement;
import com.documentum.services.config.IConfigLookup;
import com.documentum.services.config.IConfigService;
import com.documentum.services.config.IContext;

import pro.documentum.util.ISessionInvoker;
import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class RepositoryConfigProvider implements IConfigProvider {

    public static final long DEFAULT_REFRESH = 10 * 60 * 1000;

    private static final ThreadLocal<IDfSession> SESSION_HOLDER = new ThreadLocal<>();

    private IConfigService _configService;

    private ConfigServiceFactory _configServiceFactory;

    private String _folderPath;

    private String _appName;

    private final Long _refreshInterval;

    private RepositoryConfigProvider(final String folderPath,
            final String appName, final long refreshInterval) {
        super();
        _folderPath = folderPath;
        _appName = appName;
        _refreshInterval = refreshInterval;
        _configServiceFactory = ConfigServiceFactory.getInstance(this);
    }

    public static RepositoryConfigProvider getInstance(final String path,
            final String name) {
        return getInstance(path, name, DEFAULT_REFRESH);
    }

    public static RepositoryConfigProvider getInstance(final String path,
            final String name, final long refreshInterval) {
        return new RepositoryConfigProvider(path, name, refreshInterval);
    }

    public String getRootFolderPath() {
        return _folderPath;
    }

    public String getAppName() {
        return _appName;
    }

    public long getRefreshInterval() {
        return _refreshInterval;
    }

    public IDfSession getSession() {
        return SESSION_HOLDER.get();
    }

    private <T> T useSession(final IDfSession session,
            final ISessionInvoker<T, Void, RuntimeException> invoker) {
        try {
            SESSION_HOLDER.set(session);
            return invoker.invoke(null);
        } finally {
            SESSION_HOLDER.remove();
        }
    }

    @Override
    public String lookupString(final IDfSession session, final String path,
            final IContext context) {
        return useSession(session, new LookupString(context, path));
    }

    @Override
    public boolean lookupBoolean(final IDfSession session, final String path,
            final IContext context) {
        return lookupBoolean(session, path, context, false);
    }

    @Override
    public boolean lookupBoolean(final IDfSession session, final String path,
            final IContext context, final boolean defaultValue) {
        String value = lookupString(session, path, context);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "t".equalsIgnoreCase(value);
    }

    @Override
    public IConfigElement lookupElement(final IDfSession session,
            final String path, final IContext context) {
        return useSession(session, new LookupElement(context, path));
    }

    public <T> T loadElement(final IDfSession session,
            final IConfigLoader<T> loader) {
        return useSession(session, new LoadElement<>(loader));
    }

    private IConfigService getConfigService() {
        try {
            IConfigService configService = _configServiceFactory
                    .getConfigService();
            if (configService != null) {
                _configService = configService;
            }
        } catch (Exception ex) {
            Logger.error(ex);
        } catch (Error e) {
            Logger.error(e);
            throw e;
        }
        return _configService;
    }

    public interface IConfigLoader<T> {

        T apply(IConfigLookup lookup);

    }

    private class LookupElement implements
            ISessionInvoker<IConfigElement, Void, RuntimeException> {

        private final IContext _context;

        private final String _path;

        LookupElement(final IContext context, final String path) {
            _context = context;
            _path = path;
        }

        @Override
        public IConfigElement invoke(final Void session)
            throws RuntimeException {
            Objects.requireNonNull(_context, "Context is null");
            IConfigService configService = getConfigService();
            Objects.requireNonNull(configService,
                    "Configservice is not initialized");
            IConfigLookup lookup = configService.getConfigLookup();
            return lookup.lookupElement(_path, _context);
        }

    }

    private class LoadElement<T> implements
            ISessionInvoker<T, Void, RuntimeException> {

        private final IConfigLoader<T> _loader;

        LoadElement(final IConfigLoader<T> loader) {
            _loader = loader;
        }

        @Override
        public T invoke(final Void session) throws RuntimeException {
            IConfigService configService = getConfigService();
            Objects.requireNonNull(configService,
                    "Configservice is not initialized");
            IConfigLookup lookup = configService.getConfigLookup();
            return _loader.apply(lookup);
        }

    }

    private class LookupString implements
            ISessionInvoker<String, Void, RuntimeException> {

        private final IContext _context;

        private final String _path;

        LookupString(final IContext context, final String path) {
            _context = context;
            _path = path;
        }

        @Override
        public String invoke(final Void session) throws RuntimeException {
            Objects.requireNonNull(_context, "Context is null");
            IConfigService configService = getConfigService();
            Objects.requireNonNull(configService,
                    "Configservice is not initialized");
            IConfigLookup lookup = configService.getConfigLookup();
            if (lookup == null) {
                return null;
            }
            return lookup.lookupString(_path, _context);
        }

    }

}
