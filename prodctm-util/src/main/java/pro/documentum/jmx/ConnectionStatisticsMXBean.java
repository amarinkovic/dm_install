package pro.documentum.jmx;

import com.documentum.fc.client.impl.connection.docbase.IDocbaseConnectionManager;
import com.documentum.fc.impl.RuntimeContext;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ConnectionStatisticsMXBean implements IConnectionStatisticsMXBean {

    private IDocbaseConnectionManager _connectionManager;

    public ConnectionStatisticsMXBean() {
        super();
    }

    private IDocbaseConnectionManager getConnectionManager() {
        if (_connectionManager != null) {
            return _connectionManager;
        }
        synchronized (this) {
            _connectionManager = RuntimeContext.getInstance()
                    .getSessionFactory().getDocbaseConnectionManager();
            return _connectionManager;
        }
    }

    private Object[] getConnectionDetails() {
        return getConnectionManager().getConnectionDetails();
    }

    @Override
    public int getMaximumConnectionsCount() {
        return (Integer) getConnectionDetails()[0];
    }

    @Override
    public int getUnusedConnectionsCount() {
        return (Integer) getConnectionDetails()[1];
    }

    @Override
    public int getUsedConnectionsCount() {
        return (Integer) getConnectionDetails()[2];
    }

    @Override
    public int getTransitionConnectionsCount() {
        return (Integer) getConnectionDetails()[3];
    }

}
