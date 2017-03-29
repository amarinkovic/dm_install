package pro.documentum.jmx;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfMBeanServerProvider;
import com.documentum.fc.impl.util.ObjectNameUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractMBeanServerProvider implements
        IDfMBeanServerProvider {

    private final IDfMBeanServerProvider _wrapped;

    private final MBeanServer _server;

    private final String _domain;

    public AbstractMBeanServerProvider() {
        super();
        try {
            _wrapped = createProvider();
            _server = _wrapped.getMBeanServer();
            if (_server != null) {
                _domain = _server.getDefaultDomain();
                ObjectName name = ObjectNameUtil.newObjectName(_domain,
                        "Statistics", "Connections");
                _server.registerMBean(new ConnectionStatisticsMXBean(), name);
            } else {
                _domain = null;
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected abstract IDfMBeanServerProvider createProvider();

    @Override
    public MBeanServer getMBeanServer() throws DfException {
        return _server;
    }

    @Override
    public String getMBeanServerDomain() throws DfException {
        return _domain;
    }

}
