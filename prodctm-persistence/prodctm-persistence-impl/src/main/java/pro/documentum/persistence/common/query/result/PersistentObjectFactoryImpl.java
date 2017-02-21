package pro.documentum.persistence.common.query.result;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.util.DfExceptions;
import pro.documentum.util.objects.DfObjects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistentObjectFactoryImpl<E> extends
        AbstractPersistentObjectFactory<E> {

    public PersistentObjectFactoryImpl(final ExecutionContext ec,
            final AbstractClassMetaData metaData, final int[] members,
            final boolean ignoreCache) {
        super(ec, metaData, members, ignoreCache);
    }

    @Override
    public E getObject(final ExecutionContext ec, final IDfTypedObject object) {
        AbstractClassMetaData cmd = getMetaData(object);
        Table table = getTable(cmd);
        try {
            IDfTypedObject persistent = object;
            if (!isIgnoreCache()) {
                persistent = DfObjects.asPersistent(object, table.getName());
            }
            return getPojoForDBObjectForCandidate(persistent, cmd);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

}
