package pro.documentum.persistence.common.fieldmanager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfPersistentObject;

import pro.documentum.persistence.common.util.DNMetaData;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractStoreFieldManager extends
        org.datanucleus.store.fieldmanager.AbstractStoreFieldManager {

    private final Table _table;

    private final Map<String, Object> _values = new HashMap<>();

    private final IDfPersistentObject _object;

    private final FieldHelper _fieldHelper;

    public AbstractStoreFieldManager(final ExecutionContext ec,
            final IDfPersistentObject object, final AbstractClassMetaData cmd,
            final boolean insert, final Table table) {
        super(ec, cmd, insert);
        _table = table;
        _object = object;
        _fieldHelper = new FieldHelper(ec, cmd);
    }

    public AbstractStoreFieldManager(final ObjectProvider<?> op,
            final IDfPersistentObject object, final boolean insert,
            final Table table) {
        super(op, insert);
        _table = table;
        _object = object;
        _fieldHelper = new FieldHelper(ec, cmd);
    }

    private static void store(final Map<String, List<Object>> result,
            final Map<String, ?> tmp) {
        for (String attrName : result.keySet()) {
            result.get(attrName).add(tmp.get(attrName));
        }
    }

    protected MemberColumnMapping getMemberColumnMappingForEmbeddedMember(
            final List<AbstractMemberMetaData> mmds) {
        return _table.getMemberColumnMappingForEmbeddedMember(mmds);
    }

    public Map<String, Object> getValues() {
        return _values;
    }

    protected void storeSingleField(final int fieldNumber, final Object value) {
        storeSingleField(getFieldHelper().getMemberMetadata(fieldNumber), value);
    }

    protected void storeSingleField(final AbstractMemberMetaData mmd,
            final Object value) {
        if (!isStorable(mmd)) {
            return;
        }
        String attrName = DNMetaData.getColumnName(mmd);
        putValue(attrName, value);
    }

    protected final void putValue(final String attrName, final Object value) {
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

    protected void storeEmbedded(final AbstractMemberMetaData mmd,
            final Object value) {
        AbstractClassMetaData emcmd = getFieldHelper().getMetaDataForClass(
                DNMetaData.getElementClass(mmd));
        if (emcmd == null) {
            throw new NucleusUserException("Field " + mmd.getFullFieldName()
                    + " marked as embedded but no such metadata");
        }

        if (!mmd.hasContainer()) {
            _values.putAll(storeSingleEmbedded(emcmd, mmd, value));
            return;
        }

        if (mmd.hasCollection()) {
            _values.putAll(storeEmbeddedCollection(emcmd, mmd,
                    (Collection<?>) value));
            return;
        }

        if (mmd.hasArray()) {
            _values.putAll(storeEmbeddedArray(emcmd, mmd, value));
        }
    }

    protected Map<String, List<Object>> prepareEmbeddedResult(
            final AbstractMemberMetaData mmd) {
        AbstractMemberMetaData[] embmmds = DNMetaData
                .getEmbeddedMemberMetaData(mmd);
        List<String> attrs = getFieldHelper().getAttrs(embmmds);
        Map<String, List<Object>> result = new HashMap<>();
        for (String attrName : attrs) {
            result.put(attrName, new ArrayList<>());
        }
        return result;
    }

    protected Map<String, List<Object>> storeEmbeddedCollection(
            final AbstractClassMetaData emcmd,
            final AbstractMemberMetaData mmd, final Collection<?> values) {
        Map<String, List<Object>> result = prepareEmbeddedResult(mmd);
        for (Object object : values) {
            Map<String, ?> tmp = storeSingleEmbedded(emcmd, mmd, object);
            store(result, tmp);
        }
        return result;
    }

    protected Map<String, List<Object>> storeEmbeddedArray(
            final AbstractClassMetaData emcmd,
            final AbstractMemberMetaData mmd, final Object value) {
        Map<String, List<Object>> result = prepareEmbeddedResult(mmd);
        int size = Array.getLength(value);
        for (int i = 0; i < size; i++) {
            Map<String, ?> tmp = storeSingleEmbedded(emcmd, mmd,
                    Array.get(value, i));
            store(result, tmp);
        }
        return result;
    }

    protected Map<String, ?> storeSingleEmbedded(
            final AbstractClassMetaData emcmd,
            final AbstractMemberMetaData mmd, final Object value) {
        List<AbstractMemberMetaData> parentmd = Collections.singletonList(mmd);
        ObjectProvider<?> eop = ec
                .findObjectProviderForEmbedded(value, op, mmd);
        StoreFieldManager sfm = new StoreEmbeddedFieldManager(eop, _object,
                insert, parentmd, _table);
        eop.provideFields(emcmd.getAllMemberPositions(), sfm);
        return sfm.getValues();
    }

    protected FieldHelper getFieldHelper() {
        return _fieldHelper;
    }

}
