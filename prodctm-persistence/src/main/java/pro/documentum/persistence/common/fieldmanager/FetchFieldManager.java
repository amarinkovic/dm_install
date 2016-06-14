package pro.documentum.persistence.common.fieldmanager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.IPersistenceHandler;
import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNRelation;
import pro.documentum.persistence.common.util.DfExceptions;
import pro.documentum.util.java.Classes;
import pro.documentum.util.queries.keys.CompositeKey;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class FetchFieldManager extends AbstractFetchFieldManager {

    public FetchFieldManager(final ObjectProvider<?> op,
            final IDfTypedObject object, final Table table) {
        super(op, object, table);
    }

    public FetchFieldManager(final ExecutionContext ec,
            final IDfTypedObject object, final AbstractClassMetaData cmd,
            final Table table) {
        super(ec, object, cmd, table);
    }

    @Override
    public Object fetchObjectField(final int fieldNumber) {
        try {
            AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
            if (!DNMetaData.isPersistent(mmd)) {
                return op.provideField(fieldNumber);
            }

            boolean isEmbedded = isEmbedded(mmd);
            RelationType relationType = getRelationType(mmd);

            if (!isEmbedded) {
                return fetchNonEmbedded(mmd);
            }

            if (RelationType.isRelationSingleValued(relationType)) {
                return fetchSingleEmbedded(mmd);
            }

            if (RelationType.isRelationMultiValued(relationType)) {
                return null;
            }

            return null;
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    protected Object fetchNonEmbedded(final AbstractMemberMetaData mmd) {
        RelationType relationType = getRelationType(mmd);
        if (DNRelation.isNone(relationType)) {
            return fetchNonPersistent(mmd);
        }
        return fetchPersistent(mmd);
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Object fetchPersistent(final AbstractMemberMetaData mmd) {
        List<CompositeKey> keys = getCompositeKeys(mmd);
        AbstractClassMetaData cmd = getMetaDataForClass(DNMetaData
                .getElementClass(mmd));
        IPersistenceHandler persistenceHandler = (IPersistenceHandler) ec
                .getStoreManager().getPersistenceHandler();
        List<?> result = persistenceHandler.selectObjects(ec, cmd, keys);
        if (!mmd.hasContainer()) {
            return result.get(0);
        }
        if (mmd.hasCollection()) {
            Collection collection = Classes.newCollection(mmd.getType());
            collection.addAll(result);
            return collection;
        }
        if (mmd.hasArray()) {
            int length = result.size();
            Class elementClass = DNMetaData.getElementClass(mmd);
            Object array = Array.newInstance(elementClass, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, result.get(i));
            }
            return array;
        }
        return null;
    }

    protected List<CompositeKey> getCompositeKeys(
            final AbstractMemberMetaData mmd) {
        List<CompositeKey> result = new ArrayList<>();
        List<Reference> references = getReferences(mmd);
        int count = getValueCount(references.get(0).getColumnName());
        for (int i = 0; i < count; i++) {
            CompositeKey key = new CompositeKey();
            for (Reference reference : references) {
                Object value = getRepeating(reference.getColumnName(),
                        reference.getTargetClass(), i);
                key.add(reference.getTargetColumn(), value);
            }
            result.add(key);
        }
        return result;
    }

    protected Object fetchNonPersistent(final AbstractMemberMetaData mmd,
            final Class<?> elementClass) {
        String fieldName = getFieldNames(mmd).get(0);
        return getValue(mmd, fieldName, elementClass);
    }

    protected Object fetchNonPersistent(final AbstractMemberMetaData mmd) {
        return fetchNonPersistent(mmd, DNMetaData.getElementClass(mmd));
    }

}
