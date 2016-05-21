package pro.documentum.jdo.fieldmanager;

import java.util.Collection;
import java.util.Date;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.fieldmanager.AbstractFetchFieldManager;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfTypedObject;

import pro.documentum.jdo.util.DNClasses;
import pro.documentum.jdo.util.DNMetaData;
import pro.documentum.jdo.util.DNValues;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class FetchFieldManager extends AbstractFetchFieldManager implements
        IDocumentumFieldSupplier {

    private final Table _table;

    private final IDfTypedObject _object;

    public FetchFieldManager(final ObjectProvider<?> op,
            final IDfTypedObject object, final Table table) {
        super(op);
        if (object == null) {
            throw new NucleusException(
                    "Attempt to create FetchFieldManager for " + op
                            + " with null IDfTypedObject!");
        }
        _table = table;
        _object = object;
    }

    public FetchFieldManager(final ExecutionContext ec,
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
    }

    protected MemberColumnMapping getColumnMapping(final int fieldNumber) {
        AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
        return _table.getMemberColumnMappingForMember(mmd);
    }

    protected AbstractMemberMetaData getMemberMetadata(final int fieldNumber) {
        return cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
    }

    @Override
    public String fetchStringField(final int fieldNumber) {
        AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
        if (mmd.getValueStrategy() == IdentityStrategy.IDENTITY) {
            return DNValues.getObjectId(_object);
        }
        return fetchSingleField(fieldNumber, String.class);
    }

    @Override
    public Date fetchDateField(final int fieldNumber) {
        return fetchSingleField(fieldNumber, Date.class);
    }

    @Override
    public boolean fetchBooleanField(final int fieldNumber) {
        return fetchSingleField(fieldNumber, boolean.class);
    }

    @Override
    public int fetchIntField(final int fieldNumber) {
        return fetchSingleField(fieldNumber, int.class);
    }

    @Override
    public double fetchDoubleField(final int fieldNumber) {
        return fetchSingleField(fieldNumber, double.class);
    }

    protected <T> T fetchSingleField(final int fieldNumber, final Class<T> type) {
        MemberColumnMapping mapping = getColumnMapping(fieldNumber);
        String fieldName = mapping.getColumn(0).getName();
        return fetchSingleField(fieldName, type);
    }

    protected <T> T fetchSingleField(final String fieldName, final Class<T> type) {
        return DNValues.getSingleValue(_object, fieldName, type);
    }

    private String getFieldName(final int fieldNumber) {
        MemberColumnMapping mcm = getColumnMapping(fieldNumber);
        AbstractMemberMetaData mmd = mcm.getMemberMetaData();
        ColumnMetaData[] columnMetaDatum = null;
        ElementMetaData emd = mmd.getElementMetaData();
        if (emd != null) {
            ColumnMetaData[] cmd = emd.getColumnMetaData();
            if (cmd != null && cmd.length > 0) {
                columnMetaDatum = cmd;
            }
        }
        if (columnMetaDatum == null) {
            columnMetaDatum = mmd.getColumnMetaData();
        }
        return columnMetaDatum[0].getName();
    }

    protected <T> Collection<T> fetchAsCollection(final int fieldNumber,
            final Class<T> type,
            final Class<? extends Collection<?>> collectionType) {
        String fieldName = getFieldName(fieldNumber);
        return fetchAsCollection(fieldName, type, collectionType);
    }

    protected <T> Collection<T> fetchAsCollection(final String fieldName,
            final Class<T> type,
            final Class<? extends Collection<?>> collectionType) {
        return DNValues.getAsCollection(_object, fieldName, type,
                collectionType);
    }

    protected <T> Object fetchAsArray(final int fieldNumber,
            final Class<T> arrayClass) {
        String fieldName = getFieldName(fieldNumber);
        return fetchAsArray(fieldName, arrayClass);
    }

    protected <T> Object fetchAsArray(final String fieldName,
            final Class<T> arrayClass) {
        return DNValues.getAsArray(_object, fieldName, arrayClass);
    }

    @Override
    public Object fetchObjectField(final int fieldNumber) {
        AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
        if (mmd.getPersistenceModifier() != FieldPersistenceModifier.PERSISTENT) {
            return op.provideField(fieldNumber);
        }

        ClassLoaderResolver clr = ec.getClassLoaderResolver();
        RelationType relationType = mmd.getRelationType(clr);
        boolean isEmbedded = MetaDataUtils.getInstance().isMemberEmbedded(
                ec.getMetaDataManager(), clr, mmd, relationType, null);

        if (mmd.hasContainer()) {
            if (mmd.hasArray() || mmd.hasCollection()) {
                return fetchAsCollection(fieldNumber, mmd);
            } else if (mmd.hasMap()) {
                return null;
            }
        }
        return fetchSingleField(fieldNumber, (Class<?>) mmd.getType());
    }

    protected Object fetchAsCollection(final int fieldNumber,
            final AbstractMemberMetaData mmd) {
        AbstractClassMetaData elementMetadata = DNMetaData.getElementMetadata(
                ec, mmd);
        if (elementMetadata != null) {
            return null;
        }
        String elementType = DNMetaData.getElementClassName(ec, mmd);
        Class<?> elementClass = DNClasses.getClass(elementType);
        if (mmd.hasArray()) {
            return fetchAsArray(fieldNumber, (Class<?>) mmd.getType());
        }
        @SuppressWarnings("unchecked")
        Class<? extends Collection<?>> containerType = mmd.getType();
        return fetchAsCollection(fieldNumber, elementClass, containerType);
    }

}
