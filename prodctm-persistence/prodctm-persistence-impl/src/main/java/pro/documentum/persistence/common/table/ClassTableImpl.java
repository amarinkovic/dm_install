package pro.documentum.persistence.common.table;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.schema.table.CompleteClassTable;
import org.datanucleus.store.schema.table.SchemaVerifier;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ClassTableImpl extends CompleteClassTable {

    public ClassTableImpl(final StoreManager storeMgr,
            final AbstractClassMetaData cmd, final SchemaVerifier verifier) {
        super(storeMgr, cmd, verifier);
    }

}
