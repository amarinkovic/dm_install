package pro.documentum.persistence.common.fieldmanager;

import java.util.HashMap;
import java.util.Map;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.fieldmanager.AbstractStoreFieldManager;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class StoreFieldManager extends AbstractStoreFieldManager {

    private final Table _table;

    private final Map<String, Object> _values = new HashMap<>();

    public StoreFieldManager(final ExecutionContext ec,
            final AbstractClassMetaData cmd, final boolean insert,
            final Table table) {
        super(ec, cmd, insert);
        _table = table;
    }

    public StoreFieldManager(final ObjectProvider<?> op, final boolean insert,
            final Table table) {
        super(op, insert);
        _table = table;
    }

    public Map<String, Object> getValues() {
        return _values;
    }

    protected MemberColumnMapping getColumnMapping(final int fieldNumber) {
        AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
        return _table.getMemberColumnMappingForMember(mmd);
    }

    protected AbstractMemberMetaData getMemberMetadata(final int fieldNumber) {
        return cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
    }

    protected void storeSingleField(final int fieldNumber, final Object value) {
        if (!isStorable(fieldNumber)) {
            return;
        }
        MemberColumnMapping mapping = getColumnMapping(fieldNumber);
        String fieldName = mapping.getColumn(0).getName();
        _values.put(fieldName, value);
    }

    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
        storeSingleField(fieldNumber, value);
    }

    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
        storeSingleField(fieldNumber, value);
    }

    @Override
    public void storeCharField(final int fieldNumber, final char value) {
        storeSingleField(fieldNumber, value);
    }

    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
        storeSingleField(fieldNumber, value);
    }

    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
        storeSingleField(fieldNumber, value);
    }

    @Override
    public void storeIntField(final int fieldNumber, final int value) {
        storeSingleField(fieldNumber, value);
    }

    @Override
    public void storeLongField(final int fieldNumber, final long value) {
        storeSingleField(fieldNumber, value);
    }

    @Override
    public void storeShortField(final int fieldNumber, final short value) {
        storeSingleField(fieldNumber, value);
    }

    @Override
    public void storeStringField(final int fieldNumber, final String value) {
        storeSingleField(fieldNumber, value);
    }

    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
        if (mmd.hasContainer()) {
            if (mmd.hasArray() || mmd.hasCollection()) {
                storeSingleField(fieldNumber, value);
            }
        }
    }

}
