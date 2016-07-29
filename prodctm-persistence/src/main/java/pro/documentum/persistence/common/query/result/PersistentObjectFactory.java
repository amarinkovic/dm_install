package pro.documentum.persistence.common.query.result;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.util.DNFind;
import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DfExceptions;
import pro.documentum.util.objects.DfObjects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistentObjectFactory<E> implements IResultObjectFactory<E> {

    private final AbstractClassMetaData _metaData;

    private final int[] _members;

    private final boolean _ignoreCache;

    public PersistentObjectFactory(final AbstractClassMetaData metaData,
            final int[] members, final boolean ignoreCache) {
        _metaData = metaData;
        _members = members;
        _ignoreCache = ignoreCache;
    }

    @Override
    public E getObject(final ExecutionContext ec, final IDfTypedObject object) {
        Table table = DNMetaData.getTable(ec, _metaData);
        try {
            IDfPersistentObject persistent = DfObjects.buildObject(
                    object.getObjectSession(), object, table.getName());
            return DNFind.getPojoForDBObjectForCandidate(persistent, ec,
                    _metaData, _members, _ignoreCache);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

}
