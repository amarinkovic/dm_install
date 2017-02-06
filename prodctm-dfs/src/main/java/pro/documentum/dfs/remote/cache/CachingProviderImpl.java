package pro.documentum.dfs.remote.cache;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.Invoker;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.spi.ServiceDelegate;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.w3c.dom.Element;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class CachingProviderImpl extends Provider {

    private static final ConcurrentMap<DelegateKey, ServiceDelegate> CACHE;

    private static final Provider DELEGATE;

    static {
        CACHE = new ConcurrentHashMap<>();
        DELEGATE = getProviderLoader();
    }

    public CachingProviderImpl() {
        super();
    }

    private static Provider getProviderLoader() {
        ServiceLoader<Provider> loader = ServiceLoader.load(Provider.class);
        Iterator<Provider> iterator = loader.iterator();
        Provider provider = null;
        while (iterator.hasNext()) {
            provider = iterator.next();
            if (isSelf(provider)) {
                continue;
            }
            return provider;
        }
        provider = Provider.provider();
        if (isSelf(provider)) {
            throw new WebServiceException(
                    "Unable to createEndpointReference Provider");
        }
        return provider;
    }

    public static boolean isSelf(final Provider provider) {
        return provider instanceof CachingProviderImpl;
    }

    @Override
    public ServiceDelegate createServiceDelegate(
            final URL wsdlDocumentLocation, final QName serviceName,
            final Class<? extends Service> serviceClass) {
        DelegateKey key = new DelegateKey(wsdlDocumentLocation, serviceName,
                serviceClass);
        ServiceDelegate result = CACHE.get(key);
        if (result == null) {
            result = DELEGATE.createServiceDelegate(wsdlDocumentLocation,
                    serviceName, serviceClass);
            ServiceDelegate temp = CACHE.putIfAbsent(key, result);
            if (temp != null) {
                result = temp;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public ServiceDelegate createServiceDelegate(
            final URL wsdlDocumentLocation, final QName serviceName,
            final Class serviceClass, final WebServiceFeature... features) {
        if (features == null || features.length == 0) {
            return createServiceDelegate(wsdlDocumentLocation, serviceName,
                    serviceClass);
        }
        return DELEGATE.createServiceDelegate(wsdlDocumentLocation,
                serviceName, serviceClass, features);
    }

    @Override
    public Endpoint createEndpoint(final String bindingId,
            final Object implementor) {
        return DELEGATE.createEndpoint(bindingId, implementor);
    }

    @Override
    public Endpoint createAndPublishEndpoint(final String address,
            final Object implementor) {
        return DELEGATE.createAndPublishEndpoint(address, implementor);
    }

    @Override
    public EndpointReference readEndpointReference(final Source eprInfoset) {
        return DELEGATE.readEndpointReference(eprInfoset);
    }

    @Override
    public <T> T getPort(final EndpointReference endpointReference,
            final Class<T> serviceEndpointInterface,
            final WebServiceFeature... features) {
        return DELEGATE.getPort(endpointReference, serviceEndpointInterface,
                features);
    }

    @Override
    public W3CEndpointReference createW3CEndpointReference(
            final String address, final QName serviceName,
            final QName portName, final List<Element> metadata,
            final String wsdlDocumentLocation,
            final List<Element> referenceParameters) {
        return DELEGATE.createW3CEndpointReference(address, serviceName,
                portName, metadata, wsdlDocumentLocation, referenceParameters);
    }

    @Override
    public W3CEndpointReference createW3CEndpointReference(
            final String address, final QName interfaceName,
            final QName serviceName, final QName portName,
            final List<Element> metadata, final String wsdlDocumentLocation,
            final List<Element> referenceParameters,
            final List<Element> elements, final Map<QName, String> attributes) {
        return DELEGATE.createW3CEndpointReference(address, interfaceName,
                serviceName, portName, metadata, wsdlDocumentLocation,
                referenceParameters, elements, attributes);
    }

    @Override
    public Endpoint createAndPublishEndpoint(final String address,
            final Object implementor, final WebServiceFeature... features) {
        return DELEGATE
                .createAndPublishEndpoint(address, implementor, features);
    }

    @Override
    public Endpoint createEndpoint(final String bindingId,
            final Object implementor, final WebServiceFeature... features) {
        return DELEGATE.createEndpoint(bindingId, implementor, features);
    }

    @Override
    public Endpoint createEndpoint(final String bindingId,
            final Class<?> implementorClass, final Invoker invoker,
            final WebServiceFeature... features) {
        return DELEGATE.createEndpoint(bindingId, implementorClass, invoker,
                features);
    }

    class DelegateKey {

        private final URL _wsdlLocation;

        private final QName _serviceName;

        private final Class<? extends Service> _serviceClass;

        DelegateKey(final URL wsdlLocation, final QName serviceName,
                final Class<? extends Service> serviceClass) {
            _wsdlLocation = wsdlLocation;
            _serviceName = serviceName;
            _serviceClass = serviceClass;
        }

        public URL getWsdlLocation() {
            return _wsdlLocation;
        }

        public QName getServiceName() {
            return _serviceName;
        }

        public Class<? extends Service> getServiceClass() {
            return _serviceClass;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof DelegateKey)) {
                return false;
            }
            DelegateKey that = (DelegateKey) o;
            return Objects.equals(_wsdlLocation, that._wsdlLocation)
                    && Objects.equals(_serviceName, that._serviceName)
                    && Objects.equals(_serviceClass, that._serviceClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_wsdlLocation, _serviceName, _serviceClass);
        }

    }

}
