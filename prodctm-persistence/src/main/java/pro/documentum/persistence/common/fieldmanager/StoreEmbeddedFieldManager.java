package pro.documentum.persistence.common.fieldmanager;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.schema.table.Table;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class StoreEmbeddedFieldManager extends StoreFieldManager {

    public StoreEmbeddedFieldManager(final ExecutionContext ec,
            final AbstractClassMetaData cmd, final boolean insert,
            final Table table) {
        super(ec, cmd, insert, table);
    }

    public StoreEmbeddedFieldManager(final ObjectProvider<?> op,
            final boolean insert, final Table table) {
        super(op, insert, table);
    }

}
