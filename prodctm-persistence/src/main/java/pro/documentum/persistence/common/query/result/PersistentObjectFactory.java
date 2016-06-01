package pro.documentum.persistence.common.query.result;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;

import com.documentum.fc.client.IDfTypedObject;

import pro.documentum.persistence.common.util.DNFind;

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
        return DNFind.getPojoForDBObjectForCandidate(object, ec, _metaData,
                _members, _ignoreCache);
    }

}
