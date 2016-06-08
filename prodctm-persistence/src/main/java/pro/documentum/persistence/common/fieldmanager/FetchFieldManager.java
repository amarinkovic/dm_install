package pro.documentum.persistence.common.fieldmanager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.EmbeddedMetaData;
import org.datanucleus.metadata.FieldRole;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.ObjectProviderFactory;
import org.datanucleus.store.fieldmanager.AbstractFetchFieldManager;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNRelation;
import pro.documentum.persistence.common.util.DNValues;
import pro.documentum.persistence.common.util.DfExceptions;
import pro.documentum.util.java.Classes;

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

    protected <T> Collection<T> fetchCollection(final String fieldName,
            final Class<T> type,
            final Class<? extends Collection<?>> collectionType) {
        return DNValues.getCollection(_object, fieldName, type, collectionType);
    }

    protected <T> Object fetchArray(final String fieldName,
            final Class<T> elementClass) {
        return DNValues.getArray(_object, fieldName, elementClass);
    }

    @Override
    public Object fetchObjectField(final int fieldNumber) {
        try {
            AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
            if (!DNMetaData.isPersistent(mmd)) {
                return op.provideField(fieldNumber);
            }

            boolean isEmbedded = isEmbedded(mmd);
            RelationType relationType = getRelationType(mmd);

            if (!isEmbedded) {
                return fetchNonEmbedded(mmd, fieldNumber);
            }

            if (RelationType.isRelationSingleValued(relationType)) {
                return fetchSingleEmbedded(mmd, fieldNumber);
            }

            if (RelationType.isRelationMultiValued(relationType)) {
                return null;
            }

            return null;
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
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

    @SuppressWarnings("unchecked")
    protected Object fetchNonEmbedded(final AbstractMemberMetaData mmd,
            final int fieldNumber) {
        Class<?> elementClass = getElementClass(mmd, String.class);
        Object result = null;
        if (!mmd.hasContainer()) {
            result = fetchSingleField(fieldNumber, elementClass);
        } else if (mmd.hasArray() || mmd.hasCollection()) {
            result = fetchCollectionOrArray(fieldNumber, mmd, elementClass);
        } else {
            result = null;
        }

        RelationType relationType = getRelationType(mmd);
        if (DNRelation.isNone(relationType)) {
            return result;
        }

        if (RelationType.isRelationSingleValued(relationType)) {
            return getValueForSingleRelationField(mmd, (String) result,
                    FieldRole.ROLE_FIELD);
        }

        if (RelationType.isRelationMultiValued(relationType)) {
            if (mmd.hasArray()) {
                return getValueForMultiRelationField(mmd, (String[]) result);
            }
            if (mmd.hasCollection()) {
                return getValueForMultiRelationField(mmd,
                        (Collection<String>) result);
            }
        }

        return null;
    }

    protected Class<?> getElementClass(final AbstractMemberMetaData mmd,
            final Class<?> nonObjectClass) {
        RelationType relationType = getRelationType(mmd);
        if (!DNRelation.isNone(relationType)) {
            return nonObjectClass;
        }
        if (!mmd.hasContainer()) {
            return (Class<?>) mmd.getType();
        }
        return DNMetaData.getElementClass(ec, mmd);
    }

    protected Object fetchCollectionOrArray(final int fieldNumber,
            final AbstractMemberMetaData mmd, final Class<?> elementClass) {
        String fieldName = getFieldName(fieldNumber);
        if (mmd.hasArray()) {
            return fetchArray(fieldName, elementClass);
        }
        @SuppressWarnings("unchecked")
        Class<? extends Collection<?>> containerType = mmd.getType();
        return fetchCollection(fieldName, elementClass, containerType);
    }

    protected Object getValueForMultiRelationField(
            final AbstractMemberMetaData mmd, final String[] values) {
        Class<?> elementClass = DNMetaData.getElementClass(ec, mmd);
        Object result = Array.newInstance(elementClass, values.length);
        for (int i = 0; i < values.length; i++) {
            Object value = getValueForSingleRelationField(mmd, values[i],
                    FieldRole.ROLE_ARRAY_ELEMENT);
            Array.set(result, i, value);
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Object getValueForMultiRelationField(
            final AbstractMemberMetaData mmd, final Collection<String> values) {
        Collection collection = Classes.newCollection(mmd.getType());
        for (String id : values) {
            Object value = getValueForSingleRelationField(mmd, id,
                    FieldRole.ROLE_COLLECTION_ELEMENT);
            collection.add(value);
        }
        return collection;
    }

    protected Object getValueForSingleRelationField(
            final AbstractMemberMetaData mmd, final String value,
            final FieldRole fieldRole) {
        if (value == null) {
            return null;
        }

        AbstractClassMetaData memberCmd = getMetaDataForClass(mmd);
        if (memberCmd != null) {
            return IdentityUtils.getObjectFromIdString(value, memberCmd, ec,
                    true);
        }

        String[] implNames = getImplementationsForReferenceField(mmd, fieldRole);
        if (implNames == null || implNames.length == 0) {
            throw new NucleusUserException(
                    "We do not currently support the field type of "
                            + mmd.getFullFieldName()
                            + " which has an interdeterminate type (e.g interface or Object element types)");
        }

        // noinspection LoopStatementThatDoesntLoop
        for (String implName : implNames) {
            memberCmd = getMetaDataForClass(implName);
            return IdentityUtils.getObjectFromPersistableIdentity(value,
                    memberCmd, ec);
        }

        return null;
    }

    private String[] getImplementationsForReferenceField(
            final AbstractMemberMetaData mmd, final FieldRole fieldRole) {
        return MetaDataUtils.getInstance()
                .getImplementationNamesForReferenceField(mmd, fieldRole,
                        getClassLoaderResolver(), ec.getMetaDataManager());
    }

    protected Object fetchSingleEmbedded(final AbstractMemberMetaData mmd,
            final int fieldNumber) throws DfException {
        AbstractClassMetaData embcmd = getMetaDataForClass(mmd);
        if (embcmd == null) {
            throw new NucleusUserException("Field " + mmd.getFullFieldName()
                    + " marked as embedded but no such metadata");
        }
        embcmd = DNMetaData.getActualMetaData(_object, ec, embcmd);
        EmbeddedMetaData embmd = mmd.getEmbeddedMetaData();
        AbstractMemberMetaData[] embmmds = embmd.getMemberMetaData();
        boolean hasAllAttrs = true;
        for (AbstractMemberMetaData embmmd : embmmds) {
            String embFieldName = DNMetaData.getFieldName(embmmd);
            if (_object.hasAttr(embFieldName)) {
                continue;
            }
            hasAllAttrs = false;
            break;
        }

        if (!hasAllAttrs) {
            return null;
        }

        List<AbstractMemberMetaData> embMmds = new ArrayList<>();
        embMmds.add(mmd);
        ObjectProviderFactory opf = getObjectProviderFactory();
        ObjectProvider eop = opf.newForEmbedded(ec, embcmd, op, fieldNumber);
        FieldManager ffm = new FetchEmbeddedFieldManager(eop, _object, embMmds,
                _table);
        eop.replaceFields(embcmd.getAllMemberPositions(), ffm);
        return eop.getObject();
    }

    protected AbstractClassMetaData getMetaDataForClass(
            final AbstractMemberMetaData mmd) {
        return getMetaDataForClass(mmd.getType());
    }

    protected AbstractClassMetaData getMetaDataForClass(final Class<?> cls) {
        return getMetaDataForClass(cls.getName());
    }

    protected AbstractClassMetaData getMetaDataForClass(final String className) {
        return getMetaDataManager().getMetaDataForClass(className,
                getClassLoaderResolver());
    }

    protected ObjectProviderFactory getObjectProviderFactory() {
        return ec.getNucleusContext().getObjectProviderFactory();
    }

}
