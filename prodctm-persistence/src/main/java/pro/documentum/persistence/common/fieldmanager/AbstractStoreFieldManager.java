package pro.documentum.persistence.common.fieldmanager;

import java.util.HashMap;
import java.util.Map;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import pro.documentum.persistence.common.util.DNMetaData;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractStoreFieldManager extends
        org.datanucleus.store.fieldmanager.AbstractStoreFieldManager {

    private final Table _table;

    private final Map<String, Object> _values = new HashMap<>();

    public AbstractStoreFieldManager(final ExecutionContext ec,
            final AbstractClassMetaData cmd, final boolean insert,
            final Table table) {
        super(ec, cmd, insert);
        _table = table;
    }

    public AbstractStoreFieldManager(final ObjectProvider<?> op,
            final boolean insert, final Table table) {
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
        storeSingleField(getMemberMetadata(fieldNumber), value);
    }

    protected void storeSingleField(final AbstractMemberMetaData mmd,
            final Object value) {
        if (!isStorable(mmd)) {
            return;
        }
        String attrName = DNMetaData.getColumnName(mmd);
        _values.put(attrName, value);
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

    protected RelationType getRelationType(final AbstractMemberMetaData mmd) {
        ClassLoaderResolver clr = getClassLoaderResolver();
        return mmd.getRelationType(clr);
    }

    protected boolean isEmbedded(final AbstractMemberMetaData mmd) {
        return MetaDataUtils.getInstance().isMemberEmbedded(
                getMetaDataManager(), getClassLoaderResolver(), mmd,
                getRelationType(mmd), null);
    }

    protected MetaDataManager getMetaDataManager() {
        return ec.getMetaDataManager();
    }

    protected ClassLoaderResolver getClassLoaderResolver() {
        return ec.getClassLoaderResolver();
    }

    protected void storeSingleEmbedded(final AbstractMemberMetaData mmd,
            final Object value) {
        // todo
    }

}
