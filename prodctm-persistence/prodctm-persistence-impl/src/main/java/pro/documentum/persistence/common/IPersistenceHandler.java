package pro.documentum.persistence.common;

import java.util.List;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.StorePersistenceHandler;

import pro.documentum.util.queries.keys.CompositeKey;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IPersistenceHandler extends StorePersistenceHandler {

    <T extends CompositeKey> List<Object> selectObjects(ExecutionContext ec,
            AbstractClassMetaData cmd, List<T> keys);

}
