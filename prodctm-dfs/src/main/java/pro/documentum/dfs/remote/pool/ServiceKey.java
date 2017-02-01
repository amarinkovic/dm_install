package pro.documentum.dfs.remote.pool;

import java.util.Objects;

import com.emc.documentum.fs.rt.context.IContextHolder;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ServiceKey<T extends IContextHolder> {

    private final Class<T> _serviceClass;

    private final String _serviceModule;

    private final String _contextRoot;

    ServiceKey(final Class<T> serviceClass, final String serviceModule,
            final String contextRoot) {
        _serviceClass = Objects.requireNonNull(serviceClass);
        _serviceModule = serviceModule;
        _contextRoot = contextRoot;
    }

    Class<T> getServiceClass() {
        return _serviceClass;
    }

    String getServiceModule() {
        return _serviceModule;
    }

    String getContextRoot() {
        return _contextRoot;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceKey)) {
            return false;
        }
        ServiceKey<?> that = (ServiceKey) o;
        return Objects.equals(_serviceClass, that._serviceClass)
                && Objects.equals(_serviceModule, that._serviceModule)
                && Objects.equals(_contextRoot, that._contextRoot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_serviceClass, _serviceModule, _contextRoot);
    }

}
