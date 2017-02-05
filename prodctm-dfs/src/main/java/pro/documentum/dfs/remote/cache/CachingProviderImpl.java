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

    private static final ConcurrentMap<DelegateKey, ServiceDelegate> _cache;

    private static final Provider _delegate;

    static {
        _cache = new ConcurrentHashMap<>();
        _delegate = getProviderLoader();
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

    public static boolean isSelf(Provider provider) {
        return provider instanceof CachingProviderImpl;
    }

    @Override
    public ServiceDelegate createServiceDelegate(URL wsdlDocumentLocation,
            QName serviceName, Class<? extends Service> serviceClass) {
        DelegateKey key = new DelegateKey(wsdlDocumentLocation, serviceName,
                serviceClass);
        ServiceDelegate result = _cache.get(key);
        if (result == null) {
            result = _delegate.createServiceDelegate(wsdlDocumentLocation,
                    serviceName, serviceClass);
            _cache.put(key, result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public ServiceDelegate createServiceDelegate(URL wsdlDocumentLocation,
            QName serviceName, Class serviceClass,
            WebServiceFeature... features) {
        if (features == null || features.length == 0) {
            return createServiceDelegate(wsdlDocumentLocation, serviceName,
                    serviceClass);
        }
        return _delegate.createServiceDelegate(wsdlDocumentLocation,
                serviceName, serviceClass, features);
    }

    @Override
    public Endpoint createEndpoint(String bindingId, Object implementor) {
        return _delegate.createEndpoint(bindingId, implementor);
    }

    @Override
    public Endpoint createAndPublishEndpoint(String address, Object implementor) {
        return _delegate.createAndPublishEndpoint(address, implementor);
    }

    @Override
    public EndpointReference readEndpointReference(Source eprInfoset) {
        return _delegate.readEndpointReference(eprInfoset);
    }

    @Override
    public <T> T getPort(EndpointReference endpointReference,
            Class<T> serviceEndpointInterface, WebServiceFeature... features) {
        return _delegate.getPort(endpointReference, serviceEndpointInterface,
                features);
    }

    @Override
    public W3CEndpointReference createW3CEndpointReference(String address,
            QName serviceName, QName portName, List<Element> metadata,
            String wsdlDocumentLocation, List<Element> referenceParameters) {
        return _delegate.createW3CEndpointReference(address, serviceName,
                portName, metadata, wsdlDocumentLocation, referenceParameters);
    }

    @Override
    public W3CEndpointReference createW3CEndpointReference(String address,
            QName interfaceName, QName serviceName, QName portName,
            List<Element> metadata, String wsdlDocumentLocation,
            List<Element> referenceParameters, List<Element> elements,
            Map<QName, String> attributes) {
        return _delegate.createW3CEndpointReference(address, interfaceName,
                serviceName, portName, metadata, wsdlDocumentLocation,
                referenceParameters, elements, attributes);
    }

    @Override
    public Endpoint createAndPublishEndpoint(String address,
            Object implementor, WebServiceFeature... features) {
        return _delegate.createAndPublishEndpoint(address, implementor,
                features);
    }

    @Override
    public Endpoint createEndpoint(String bindingId, Object implementor,
            WebServiceFeature... features) {
        return _delegate.createEndpoint(bindingId, implementor, features);
    }

    @Override
    public Endpoint createEndpoint(String bindingId, Class<?> implementorClass,
            Invoker invoker, WebServiceFeature... features) {
        return _delegate.createEndpoint(bindingId, implementorClass, invoker,
                features);
    }

    public class DelegateKey {

        private final URL _wsdlLocation;

        private final QName _serviceName;

        private final Class<? extends Service> _serviceClass;

        public DelegateKey(URL wsdlLocation, QName serviceName,
                Class<? extends Service> serviceClass) {
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
        public boolean equals(Object o) {
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
