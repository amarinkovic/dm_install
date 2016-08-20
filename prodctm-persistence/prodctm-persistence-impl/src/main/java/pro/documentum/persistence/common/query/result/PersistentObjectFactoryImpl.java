package pro.documentum.persistence.common.query.result;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DfExceptions;
import pro.documentum.util.objects.DfObjects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistentObjectFactoryImpl<E> extends
        AbstractPersistentObjectFactory<E> {

    public PersistentObjectFactoryImpl(final AbstractClassMetaData metaData,
            final int[] members, final boolean ignoreCache) {
        super(metaData, members, ignoreCache);
    }

    @Override
    public E getObject(final ExecutionContext ec, final IDfTypedObject object) {
        Table table = DNMetaData.getTable(ec, getMetaData(ec, object));
        try {
            IDfPersistentObject persistent = DfObjects.buildObject(
                    object.getObjectSession(), object, table.getName());
            return getPojoForDBObjectForCandidate(persistent, ec);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

}
