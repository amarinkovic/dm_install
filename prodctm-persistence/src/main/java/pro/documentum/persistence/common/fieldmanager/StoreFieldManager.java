package pro.documentum.persistence.common.fieldmanager;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.schema.table.Table;

import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNRelation;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class StoreFieldManager extends AbstractStoreFieldManager {

    public StoreFieldManager(final ExecutionContext ec,
            final AbstractClassMetaData cmd, final boolean insert,
            final Table table) {
        super(ec, cmd, insert, table);
    }

    public StoreFieldManager(final ObjectProvider<?> op, final boolean insert,
            final Table table) {
        super(op, insert, table);
    }

    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
        if (!DNMetaData.isPersistent(mmd)) {
            return;
        }
        boolean isEmbedded = isEmbedded(mmd);
        RelationType relationType = getRelationType(mmd);

        if (!isEmbedded) {
            storeNonEmbedded(mmd, value);
        }

        if (RelationType.isRelationSingleValued(relationType)) {
            storeSingleEmbedded(mmd, value);
            return;
        }

        if (RelationType.isRelationMultiValued(relationType)) {
            storeMultipleEmbedded(mmd, value);
        }
    }

    protected void storeMultipleEmbedded(final AbstractMemberMetaData mmd,
            final Object value) {
        // todo
    }

    protected void storeNonEmbedded(final AbstractMemberMetaData mmd,
            final Object value) {
        RelationType relationType = getRelationType(mmd);
        if (DNRelation.isNone(relationType)) {
            storeNonPersistent(mmd, value);
            return;
        }
        storePersistent(mmd, value);
    }

    private void storeNonPersistent(final AbstractMemberMetaData mmd,
            final Object value) {
        storeSingleField(mmd, value);
    }

    private void storePersistent(final AbstractMemberMetaData mmd,
            final Object value) {
        // todo
    }

}
