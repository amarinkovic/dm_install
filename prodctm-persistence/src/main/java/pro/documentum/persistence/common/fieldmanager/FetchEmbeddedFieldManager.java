package pro.documentum.persistence.common.fieldmanager;

import java.util.ArrayList;
import java.util.List;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.EmbeddedMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfTypedObject;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class FetchEmbeddedFieldManager extends FetchFieldManager {

    private final List<AbstractMemberMetaData> _mmds;

    public FetchEmbeddedFieldManager(final ObjectProvider<?> op,
            final IDfTypedObject dbObject,
            final List<AbstractMemberMetaData> mmds, final Table table) {
        super(op, dbObject, table);
        _mmds = mmds;
    }

    protected MemberColumnMapping getColumnMapping(
            final AbstractMemberMetaData mmd) {
        List<AbstractMemberMetaData> embMmds = new ArrayList<>(_mmds);
        embMmds.add(mmd);
        return getMemberColumnMappingForEmbeddedMember(embMmds);
    }

    @Override
    protected <T> T getSingle(final AbstractMemberMetaData mmd,
            final Class<T> type) {
        MemberColumnMapping columnMapping = getColumnMapping(mmd);
        String fieldName = columnMapping.getColumn(0).getName();
        return getSingle(fieldName, type);
    }

    @Override
    public Object fetchObjectField(final int fieldNumber) {
        if (isSameOwner(fieldNumber)) {
            ObjectProvider[] ownerOps = ec
                    .getOwnersForEmbeddedObjectProvider(op);
            if (ownerOps == null || ownerOps.length == 0) {
                return null;
            }
            return ownerOps[0].getObject();
        }
        return null;
    }

    private boolean isSameOwner(final int fieldNumber) {
        AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
        if (_mmds.size() != 1) {
            return false;
        }
        EmbeddedMetaData embmd = _mmds.get(0).getEmbeddedMetaData();
        if (embmd == null) {
            return false;
        }
        if (embmd.getOwnerMember() == null) {
            return false;
        }
        return embmd.getOwnerMember().equals(mmd.getName());
    }

}
