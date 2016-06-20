package pro.documentum.persistence.common.fieldmanager;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfPersistentObject;

import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNRelation;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class StoreFieldManager extends AbstractStoreFieldManager {

    public StoreFieldManager(final ExecutionContext ec,
            final IDfPersistentObject object, final AbstractClassMetaData cmd,
            final boolean insert, final Table table) {
        super(ec, object, cmd, insert, table);
    }

    public StoreFieldManager(final ObjectProvider<?> op,
            final IDfPersistentObject object, final boolean insert,
            final Table table) {
        super(op, object, insert, table);
    }

    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        AbstractMemberMetaData mmd = getFieldHelper().getMemberMetadata(
                fieldNumber);
        if (!DNMetaData.isPersistent(mmd)) {
            return;
        }
        boolean isEmbedded = getFieldHelper().isEmbedded(mmd);
        if (!isEmbedded) {
            storeNonEmbedded(mmd, value);
            return;
        }
        storeEmbedded(mmd, value);
    }

    protected void storeNonEmbedded(final AbstractMemberMetaData mmd,
            final Object value) {
        RelationType relationType = getFieldHelper().getRelationType(mmd);
        if (DNRelation.isNone(relationType)) {
            storeNonPersistent(mmd, value);
            return;
        }
        storePersistent(mmd, value);
    }

    protected void storeNonPersistent(final AbstractMemberMetaData mmd,
            final Object value) {
        storeSingleField(mmd, value);
    }

    private void storePersistent(final AbstractMemberMetaData mmd,
            final Object value) {
        // todo
    }

}
