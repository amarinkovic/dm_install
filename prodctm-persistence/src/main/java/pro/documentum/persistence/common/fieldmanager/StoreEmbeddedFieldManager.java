package pro.documentum.persistence.common.fieldmanager;

import java.util.ArrayList;
import java.util.List;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfPersistentObject;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class StoreEmbeddedFieldManager extends StoreFieldManager {

    private final List<AbstractMemberMetaData> _mmds;

    StoreEmbeddedFieldManager(final ObjectProvider<?> op,
            final IDfPersistentObject object, final boolean insert,
            final List<AbstractMemberMetaData> mmds, final Table table) {
        super(op, object, insert, table);
        _mmds = mmds;
    }

    protected MemberColumnMapping getColumnMapping(
            final AbstractMemberMetaData mmd) {
        List<AbstractMemberMetaData> embMmds = new ArrayList<>(_mmds);
        embMmds.add(mmd);
        return getMemberColumnMappingForEmbeddedMember(embMmds);
    }

    @Override
    protected void storeSingleField(final AbstractMemberMetaData mmd,
            final Object value) {
        if (!isStorable(mmd)) {
            return;
        }
        MemberColumnMapping columnMapping = getColumnMapping(mmd);
        String fieldName = columnMapping.getColumn(0).getName();
        putValue(fieldName, value);
    }

    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        AbstractMemberMetaData mmd = getFieldHelper().getMemberMetadata(
                fieldNumber);
        storeNonPersistent(mmd, value);
    }

}
