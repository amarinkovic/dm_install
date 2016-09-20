package pro.documentum.persistence.common.fieldmanager;

import java.util.ArrayList;
import java.util.List;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfTypedObject;

import pro.documentum.persistence.common.IPersistenceHandler;
import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNRelation;
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
        AbstractMemberMetaData mmd = getFieldHelper().getMemberMetadata(
                fieldNumber);
        if (!DNMetaData.isPersistent(mmd)) {
            return op.provideField(fieldNumber);
        }
        boolean isEmbedded = getFieldHelper().isEmbedded(mmd);
        if (!isEmbedded) {
            return fetchNonEmbedded(mmd);
        }
        return fetchEmbedded(mmd);
    }

    protected Object fetchNonEmbedded(final AbstractMemberMetaData mmd) {
        RelationType relationType = getFieldHelper().getRelationType(mmd);
        if (DNRelation.isNone(relationType)) {
            return fetchNonPersistent(mmd);
        }
        return fetchPersistent(mmd);
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Object fetchPersistent(final AbstractMemberMetaData mmd) {
        List<CompositeKey> keys = getCompositeKeys(mmd);
        AbstractClassMetaData cmd = getFieldHelper().getMetaDataForClass(
                DNMetaData.getElementClass(mmd));
        IPersistenceHandler persistenceHandler = (IPersistenceHandler) ec
                .getStoreManager().getPersistenceHandler();
        List<?> result = persistenceHandler.selectObjects(ec, cmd, keys);
        return asRequiredType(mmd, result);
    }

    protected List<CompositeKey> getCompositeKeys(
            final AbstractMemberMetaData mmd) {
        List<CompositeKey> result = new ArrayList<>();
        List<Reference> references = getFieldHelper().getReferences(mmd);
        int count = getValueCount(references.get(0).getColumnName());
        for (int i = 0; i < count; i++) {
            CompositeKey key = new CompositeKey();
            for (Reference reference : references) {
                Object value = getRepeating(reference.getColumnName(),
                        String.class, i);
                key.add(reference.getTargetColumn(), value);
            }
            result.add(key);
        }
        return result;
    }

    protected Object fetchNonPersistent(final AbstractMemberMetaData mmd,
            final Class<?> elementClass) {
        String attrName = DNMetaData.getColumnName(mmd);
        return getValue(mmd, attrName, elementClass);
    }

    protected Object fetchNonPersistent(final AbstractMemberMetaData mmd) {
        return fetchNonPersistent(mmd, DNMetaData.getElementClass(mmd));
    }

}
