package pro.documentum.persistence.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.PersistenceNucleusContext;
import org.datanucleus.Transaction;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.store.AbstractStoreManager;
import org.datanucleus.store.NucleusConnection;
import org.datanucleus.store.NucleusConnectionImpl;
import org.datanucleus.store.StoreData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.connection.ConnectionFactory;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.table.CompleteClassTable;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.jdo.DocumentumPersistenceManager;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DocumentumStoreManager extends AbstractStoreManager {

    public static final String PREFIX = "dctm";

    public DocumentumStoreManager(final ClassLoaderResolver clr,
            final PersistenceNucleusContext nucleusContext,
            final Map<String, Object> props) {
        super(PREFIX, clr, nucleusContext, props);
        persistenceHandler = new DocumentumPersistenceHandler(this);
    }

    public static String getDocbaseName(final String url) {
        return url.substring(DocumentumStoreManager.PREFIX.length() + 1);
    }

    private static IDfLoginInfo getLoginInfo(
            final ExecutionContext executionContext) {
        if (executionContext == null) {
            return null;
        }
        Object owner = executionContext.getOwner();
        if (!(owner instanceof IDocumentumCredentialsHolder)) {
            return null;
        }
        IDocumentumCredentialsHolder ch = (IDocumentumCredentialsHolder) owner;
        return new DfLoginInfo(ch.getUserName(), ch.getPassword());
    }

    @Override
    public NucleusConnection getNucleusConnection(
            final ExecutionContext executionContext) {
        ConnectionFactory connectionFactory = connectionMgr
                .lookupConnectionFactory(primaryConnectionFactoryName);
        ExecutionContext connectionContext = executionContext;
        Map<String, Object> options = new HashMap<>();
        options.put(DocumentumPersistenceManager.OPTION_LOGININFO,
                getLoginInfo(executionContext));
        Transaction transaction = null;
        final boolean enlisted = executionContext.getTransaction().isActive();
        if (enlisted) {
            transaction = executionContext.getTransaction();
        } else {
            connectionContext = null;
        }
        final ManagedConnection managedConnection = connectionFactory
                .getConnection(connectionContext, transaction, options);
        managedConnection.lock();
        Runnable closeRunnable = new Runnable() {
            public void run() {
                managedConnection.unlock();
                if (!enlisted) {
                    managedConnection.close();
                }
            }
        };
        return new NucleusConnectionImpl(managedConnection.getConnection(),
                closeRunnable);
    }

    @Override
    public Collection<String> getSupportedOptions() {
        Set<String> result = new HashSet<>();
        result.add(StoreManager.OPTION_DATASTORE_ID);
        return result;
    }

    public void manageClasses(final ExecutionContext ec,
            final String... classNames) {
        if (classNames == null) {
            return;
        }
        ManagedConnection mconn = getConnection(ec);
        try {
            IDfSession session = (IDfSession) mconn.getConnection();
            manageClasses(ec.getClassLoaderResolver(), session, classNames);
        } finally {
            mconn.release();
        }
    }

    @Override
    public Object getStrategyValue(final ExecutionContext ec,
            final AbstractClassMetaData cmd, final int absoluteFieldNumber) {
        DNMetaData.getStoreData(ec, cmd);
        return super.getStrategyValue(ec, cmd, absoluteFieldNumber);
    }

    public void manageClasses(final ClassLoaderResolver clr,
            final IDfSession session, final String... classNames) {
        if (classNames == null) {
            return;
        }

        String[] filteredClassNames = getNucleusContext().getTypeManager()
                .filterOutSupportedSecondClassNames(classNames);

        List<AbstractClassMetaData> abstractClassMetaDatas = getMetaDataManager()
                .getReferencedClasses(filteredClassNames, clr);

        for (AbstractClassMetaData abstractClassMetaData : abstractClassMetaDatas) {
            ClassMetaData classMetaData = (ClassMetaData) abstractClassMetaData;
            if (classMetaData.isAbstract()) {
                continue;
            }
            if (classMetaData.getPersistenceModifier() != ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                continue;
            }

            String fullClassName = classMetaData.getFullClassName();
            if (storeDataMgr.managesClass(fullClassName)) {
                continue;
            }

            StoreData sd = storeDataMgr.get(fullClassName);
            if (sd == null) {
                CompleteClassTable table = new CompleteClassTable(this,
                        classMetaData, null);
                sd = newStoreData(classMetaData, clr);
                sd.setTable(table);
                registerStoreData(sd);
            }
        }
    }

}
