package pro.documentum.jdo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.PersistenceNucleusContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.store.AbstractStoreManager;
import org.datanucleus.store.StoreData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.table.CompleteClassTable;

import com.documentum.fc.client.IDfSession;

import pro.documentum.jdo.util.DNMetaData;

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

    @Override
    public Collection<String> getSupportedOptions() {
        Set<String> result = new HashSet<String>();
        result.add(StoreManager.OPTION_DATASTORE_ID);
        return result;
    }

    @Override
    public void manageClasses(final ClassLoaderResolver clr,
            final String... classNames) {
        if (classNames == null) {
            return;
        }
        ManagedConnection mconn = getConnection(-1);
        try {
            IDfSession session = (IDfSession) mconn.getConnection();
            manageClasses(clr, session, classNames);
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
