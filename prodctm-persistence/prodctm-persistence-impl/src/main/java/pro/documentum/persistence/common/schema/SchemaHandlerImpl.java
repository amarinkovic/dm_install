package pro.documentum.persistence.common.schema;

import java.util.HashMap;
import java.util.Map;

import org.datanucleus.store.StoreManager;
import org.datanucleus.store.schema.AbstractStoreSchemaHandler;
import org.datanucleus.store.schema.StoreSchemaData;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class SchemaHandlerImpl extends AbstractStoreSchemaHandler {

    private final Map<String, StoreSchemaData> _schemaDataByName = new HashMap<>();

    public SchemaHandlerImpl(final StoreManager storeMgr) {
        super(storeMgr);
    }

    @Override
    public void clear() {
        _schemaDataByName.clear();
    }

}
