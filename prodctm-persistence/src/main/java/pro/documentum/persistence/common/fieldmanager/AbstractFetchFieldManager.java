package pro.documentum.persistence.common.fieldmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
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
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfDocbaseConstants;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNRelation;
import pro.documentum.persistence.common.util.DNValues;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractFetchFieldManager extends
        org.datanucleus.store.fieldmanager.AbstractFetchFieldManager implements
        IDocumentumFieldSupplier {

    private final Table _table;

    private final IDfTypedObject _object;

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
    }

    protected MemberColumnMapping getColumnMapping(final int fieldNumber) {
        AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
        return _table.getMemberColumnMappingForMember(mmd);
    }

    @Override
    public String fetchStringField(final int fieldNumber) {
        AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
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
        return getSingle(getMemberMetadata(fieldNumber), type);
    }

    protected final <T> T getSingle(final AbstractMemberMetaData mmd,
            final Class<T> type) {
        String fieldName = getFieldNames(mmd).get(0);
        return getSingle(fieldName, type);
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
        if (!mmd.hasContainer()) {
            return getSingle(fieldName, elementClass);
        }
        return getCollectionOrArray(mmd, fieldName, elementClass);
    }

    protected List<Reference> getReferences(final AbstractMemberMetaData mmd) {
        if (isIdentityMapping(mmd)) {
            Reference reference = new Reference(getFieldNames(mmd).get(0),
                    String.class, DfDocbaseConstants.R_OBJECT_ID);
            return Collections.singletonList(reference);
        }
        List<Reference> result = new ArrayList<>();
        List<String> names = getFieldNames(mmd);
        List<Class<?>> classes = getFieldClasses(mmd);
        List<String> targets = getFieldTargets(mmd);
        for (int i = 0, n = names.size(); i < n; i++) {
            Reference reference = new Reference(names.get(i), classes.get(i),
                    targets.get(i));
            result.add(reference);
        }
        return result;
    }

    protected boolean isIdentityMapping(final AbstractMemberMetaData mmd) {
        ColumnMetaData[] columnMetaDatum = getColumnMetaDatum(mmd);
        if (columnMetaDatum.length != 1) {
            return false;
        }
        ColumnMetaData cmd = columnMetaDatum[0];
        String targetMember = cmd.getTargetMember();
        String targetColumn = cmd.getTarget();
        if (StringUtils.isBlank(targetMember)
                && StringUtils.isBlank(targetColumn)) {
            return true;
        }
        return false;
    }

    protected List<String> getFieldNames(final AbstractMemberMetaData mmd) {
        List<String> result = new ArrayList<>();
        for (ColumnMetaData cmd : getColumnMetaDatum(mmd)) {
            result.add(cmd.getName());
        }
        return result;
    }

    protected List<Class<?>> getFieldClasses(final AbstractMemberMetaData mmd) {
        Class<?> targetType = DNMetaData.getElementClass(mmd);
        RelationType relationType = getRelationType(mmd);
        if (DNRelation.isNone(relationType)) {
            return Collections.<Class<?>> singletonList(targetType);
        }
        AbstractClassMetaData cmd = getMetaDataForClass(targetType);
        List<Class<?>> result = new ArrayList<>();
        for (ColumnMetaData col : getColumnMetaDatum(mmd)) {
            String targetMember = col.getTargetMember();
            AbstractMemberMetaData tmmd = cmd
                    .getMetaDataForMember(targetMember);
            result.add(tmmd.getType());
        }
        return result;
    }

    protected List<String> getFieldTargets(final AbstractMemberMetaData mmd) {
        Class<?> targetType = DNMetaData.getElementClass(mmd);
        AbstractClassMetaData cmd = getMetaDataForClass(targetType);
        List<String> result = new ArrayList<>();
        for (ColumnMetaData col : getColumnMetaDatum(mmd)) {
            String targetField = col.getTarget();
            if (StringUtils.isNotBlank(targetField)) {
                result.add(targetField);
                continue;
            }
            String targetMember = col.getTargetMember();
            AbstractMemberMetaData tmmd = cmd
                    .getMetaDataForMember(targetMember);
            result.add(getFieldNames(tmmd).get(0));
        }
        return result;
    }

    protected ColumnMetaData[] getColumnMetaDatum(
            final AbstractMemberMetaData mmd) {
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
        return columnMetaDatum;
    }

    private <T, C extends Collection<?>> Collection<T> getCollection(
            final String fieldName, final Class<T> type,
            final Class<C> collectionType) {
        return DNValues.getCollection(_object, fieldName, type, collectionType);
    }

    private <T> Object getArray(final String fieldName,
            final Class<T> elementClass) {
        return DNValues.getArray(_object, fieldName, elementClass);
    }

    @SuppressWarnings("unchecked")
    private Object getCollectionOrArray(final AbstractMemberMetaData mmd,
            final String fieldName, final Class<?> elementClass) {
        if (mmd.hasContainer()) {
            if (mmd.hasArray()) {
                return getArray(fieldName, elementClass);
            }
            if (mmd.hasCollection()) {
                Class<? extends Collection<?>> containerType = mmd.getType();
                return getCollection(fieldName, elementClass, containerType);
            }
        }
        return null;
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

    protected AbstractMemberMetaData getMemberMetadata(final int fieldNumber) {
        return cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
    }

    protected Object fetchSingleEmbedded(final AbstractMemberMetaData mmd)
        throws DfException {
        AbstractClassMetaData embcmd = getMetaDataForClass(mmd);
        if (embcmd == null) {
            throw new NucleusUserException("Field " + mmd.getFullFieldName()
                    + " marked as embedded but no such metadata");
        }
        embcmd = DNMetaData.getActual(_object, ec, embcmd);
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
        ObjectProvider<?> eop = opf.newForEmbedded(ec, embcmd, op,
                mmd.getAbsoluteFieldNumber());
        FieldManager ffm = new FetchEmbeddedFieldManager(eop, _object, embMmds,
                _table);
        eop.replaceFields(embcmd.getAllMemberPositions(), ffm);
        return eop.getObject();
    }

    protected String[] getImplementationsForReferenceField(
            final AbstractMemberMetaData mmd) {
        String[] implNames = getImplementationsForReferenceField(mmd,
                getFieldRole(mmd));
        if (implNames == null || implNames.length == 0) {
            throw new NucleusUserException(
                    "We do not currently support the field type of "
                            + mmd.getFullFieldName()
                            + " which has an interdeterminate type (e.g interface or Object element types)");
        }
        return implNames;
    }

    protected String[] getImplementationsForReferenceField(
            final AbstractMemberMetaData mmd, final FieldRole fieldRole) {
        return MetaDataUtils.getInstance()
                .getImplementationNamesForReferenceField(mmd, fieldRole,
                        getClassLoaderResolver(), ec.getMetaDataManager());
    }

    protected FieldRole getFieldRole(final AbstractMemberMetaData mmd) {
        if (mmd.hasContainer()) {
            if (mmd.hasArray()) {
                return FieldRole.ROLE_ARRAY_ELEMENT;
            }
            if (mmd.hasCollection()) {
                return FieldRole.ROLE_COLLECTION_ELEMENT;
            }
        } else {
            return FieldRole.ROLE_FIELD;
        }
        return null;
    }

    protected int getValueCount(final String attrName) {
        return DNValues.getValueCount(_object, attrName);
    }

}
