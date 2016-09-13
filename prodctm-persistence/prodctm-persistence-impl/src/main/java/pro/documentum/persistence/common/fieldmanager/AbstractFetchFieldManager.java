package pro.documentum.persistence.common.fieldmanager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.ObjectProviderFactory;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfTypedObject;

import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNValues;
import pro.documentum.util.java.Collections;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractFetchFieldManager extends
        org.datanucleus.store.fieldmanager.AbstractFetchFieldManager implements
        IDocumentumFieldSupplier {

    private final Table _table;

    private final IDfTypedObject _object;

    private final FieldHelper _fieldHelper;

    public AbstractFetchFieldManager(final ObjectProvider<?> op,
            final IDfTypedObject object, final Table table) {
        super(op);
        if (object == null) {
            throw new NucleusException(
                    "Attempt to create FetchFieldManager for " + op
                            + " with null IDfTypedObject!");
        }
        _table = table;
        _object = object;
        _fieldHelper = new FieldHelper(ec, cmd);
    }

    public AbstractFetchFieldManager(final ExecutionContext ec,
            final IDfTypedObject object, final AbstractClassMetaData cmd,
            final Table table) {
        super(ec, cmd);
        if (object == null) {
            throw new NucleusException(
                    "Attempt to create FetchFieldManager for " + op
                            + " with null IDfTypedObject!");
        }
        _table = table;
        _object = object;
        _fieldHelper = new FieldHelper(ec, cmd);
    }

    protected MemberColumnMapping getMemberColumnMappingForEmbeddedMember(
            final List<AbstractMemberMetaData> mmds) {
        return _table.getMemberColumnMappingForEmbeddedMember(mmds);
    }

    @Override
    public String fetchStringField(final int fieldNumber) {
        AbstractMemberMetaData mmd = getFieldHelper().getMemberMetadata(
                fieldNumber);
        if (mmd.getValueStrategy() == IdentityStrategy.IDENTITY) {
            return DNValues.getObjectId(_object);
        }
        return getSingle(fieldNumber, String.class);
    }

    @Override
    public Date fetchDateField(final int fieldNumber) {
        return getSingle(fieldNumber, Date.class);
    }

    @Override
    public boolean fetchBooleanField(final int fieldNumber) {
        return getSingle(fieldNumber, boolean.class);
    }

    @Override
    public int fetchIntField(final int fieldNumber) {
        return getSingle(fieldNumber, int.class);
    }

    @Override
    public double fetchDoubleField(final int fieldNumber) {
        return getSingle(fieldNumber, double.class);
    }

    protected final <T> T getSingle(final int fieldNumber, final Class<T> type) {
        return getSingle(getFieldHelper().getMemberMetadata(fieldNumber), type);
    }

    protected <T> T getSingle(final AbstractMemberMetaData mmd,
            final Class<T> type) {
        String attrName = DNMetaData.getColumnName(mmd);
        return getSingle(attrName, type);
    }

    protected final <T> T getSingle(final String fieldName, final Class<T> type) {
        return DNValues.getSingleValue(_object, fieldName, type);
    }

    protected final <T> T getRepeating(final String fieldName,
            final Class<T> type, final int index) {
        return DNValues.getSingleValue(_object, fieldName, index, type);
    }

    protected final Object getValue(final AbstractMemberMetaData mmd,
            final String fieldName, final Class<?> elementClass) {
        int valueCount = 1;
        if (mmd.hasContainer()) {
            valueCount = getValueCount(fieldName);
        }
        List<Object> results = new ArrayList<>();
        for (int i = 0; i < valueCount; i++) {
            results.add(getRepeating(fieldName, elementClass, i));
        }
        return asRequiredType(mmd, results);
    }

    protected final Object fetchEmbedded(final AbstractMemberMetaData mmd) {
        AbstractClassMetaData embcmd = getFieldHelper().getMetaDataForClass(
                DNMetaData.getElementClass(mmd));
        if (embcmd == null) {
            throw new NucleusUserException("Field " + mmd.getFullFieldName()
                    + " marked as embedded but no such metadata");
        }
        AbstractMemberMetaData[] embmmds = DNMetaData
                .getEmbeddedMemberMetaData(mmd);
        if (!checkAttrs(embmmds)) {
            return null;
        }
        int valueCount = 1;
        if (mmd.hasContainer()) {
            valueCount = getValueCount(embmmds);
        }
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < valueCount; i++) {
            result.add(fetchSingleEmbedded(embcmd, mmd, i));
        }
        return asRequiredType(mmd, result);
    }

    @SuppressWarnings("unchecked")
    protected Object asRequiredType(final AbstractMemberMetaData mmd,
            final List<?> result) {
        if (!mmd.hasContainer()) {
            return result.get(0);
        }
        if (mmd.hasCollection()) {
            Collection collection = Collections.newCollection(mmd.getType());
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

    protected Object fetchSingleEmbedded(final AbstractClassMetaData embcmd,
            final AbstractMemberMetaData mmd, final int index) {
        List<AbstractMemberMetaData> parentmd = new ArrayList<>();
        parentmd.add(mmd);
        ObjectProviderFactory objectProviderFactory = getFieldHelper()
                .getObjectProviderFactory();
        ObjectProvider<?> objectProvider = objectProviderFactory
                .newForEmbedded(ec, embcmd, op, mmd.getAbsoluteFieldNumber());
        FieldManager ffm = new FetchEmbeddedFieldManager(objectProvider,
                _object, parentmd, _table, index);
        objectProvider.replaceFields(embcmd.getAllMemberPositions(), ffm);
        return objectProvider.getObject();
    }

    protected boolean checkAttrs(final AbstractMemberMetaData[] embmmds) {
        for (String attrName : getFieldHelper().getAttrs(embmmds)) {
            if (!DNValues.hasAttr(_object, attrName)) {
                return false;
            }
        }
        return true;
    }

    protected int getValueCount(final AbstractMemberMetaData[] embmmds) {
        String attrName = DNMetaData.getColumnName(embmmds[0]);
        return getValueCount(attrName);
    }

    protected int getValueCount(final String attrName) {
        return DNValues.getValueCount(_object, attrName);
    }

    protected FieldHelper getFieldHelper() {
        return _fieldHelper;
    }

}
