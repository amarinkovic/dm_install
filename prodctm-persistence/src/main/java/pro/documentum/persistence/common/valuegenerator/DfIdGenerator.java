package pro.documentum.persistence.common.valuegenerator;

import java.util.Properties;

import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.table.Table;
import org.datanucleus.store.valuegenerator.AbstractDatastoreGenerator;
import org.datanucleus.store.valuegenerator.ValueGenerationBlock;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import pro.documentum.util.objects.DfObjects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DfIdGenerator extends AbstractDatastoreGenerator<String> {

    public DfIdGenerator(final String name, final Properties props) {
        super(name, props);
    }

    @Override
    protected ValueGenerationBlock<String> reserveBlock(final long size) {
        String className = properties.getProperty("class-name");
        Table table = storeMgr.getStoreDataForClass(className).getTable();
        String typeName = table.getName();
        try {
            ManagedConnection conn = connectionProvider.retrieveConnection();
            return new ValueGenerationBlock<>(DfObjects.makeIds(
                    (IDfSession) conn.getConnection(), typeName, (int) size));
        } catch (DfException ex) {
            throw new NucleusDataStoreException(ex.getMessage(), ex);
        } finally {
            connectionProvider.releaseConnection();
        }
    }

    @Override
    public ConnectionPreference getConnectionPreference() {
        return ConnectionPreference.EXISTING;
    }

}
