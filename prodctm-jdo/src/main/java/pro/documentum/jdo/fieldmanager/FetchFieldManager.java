package pro.documentum.jdo.fieldmanager;

import java.util.Date;
import java.util.List;

import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.fieldmanager.AbstractFetchFieldManager;
import org.datanucleus.store.schema.table.MemberColumnMapping;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;

import pro.documentum.jdo.util.DNMetaData;
import pro.documentum.jdo.util.DNValues;
import pro.documentum.jdo.util.Nucleus;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class FetchFieldManager extends AbstractFetchFieldManager implements
        IDocumentumFieldSupplier {

    private final Table _table;

    private final IDfTypedObject _object;

    public FetchFieldManager(final ObjectProvider op,
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
        return _table
                .getMemberColumnMappingForMember(getMemberMetadata(fieldNumber));
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
        return fetchSingleField(fieldNumber, IDfValue.DF_STRING);
    }

    @Override
    public Date fetchDateField(final int fieldNumber) {
        return ((IDfTime) fetchSingleField(fieldNumber, IDfValue.DF_TIME))
                .getDate();
    }

    @Override
    public boolean fetchBooleanField(final int fieldNumber) {
        return fetchSingleField(fieldNumber, IDfValue.DF_BOOLEAN);
    }

    @Override
    public int fetchIntField(final int fieldNumber) {
        return fetchSingleField(fieldNumber, IDfValue.DF_INTEGER);
    }

    @Override
    public double fetchDoubleField(final int fieldNumber) {
        return fetchSingleField(fieldNumber, IDfValue.DF_DOUBLE);
    }

    protected <T> T fetchSingleField(final int fieldNumber, final int type) {
        MemberColumnMapping mapping = getColumnMapping(fieldNumber);
        String fieldName = mapping.getColumn(0).getName();
        return fetchSingleField(fieldName, type);
    }

    protected <T> T fetchSingleField(final String fieldName, final int type) {
        return DNValues.getSingleValue(_object, fieldName, type);
    }

    protected <T> List<T> fetchRepeatingField(final int fieldNumber,
            final int type) {
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
        String fieldName = columnMetaDatum[0].getName();
        return fetchRepeatingField(fieldName, type);
    }

    protected <T> List<T> fetchRepeatingField(final String fieldName) {
        return DNValues.getRepeatingValue(_object, fieldName);
    }

    protected <T> List<T> fetchRepeatingField(final String fieldName,
            final int type) {
        return DNValues.getRepeatingValue(_object, fieldName, type);
    }

    @Override
    public Object fetchObjectField(final int fieldNumber) {
        AbstractMemberMetaData mmd = getMemberMetadata(fieldNumber);
        if (mmd.hasContainer()) {
            if (mmd.hasArray() || mmd.hasCollection()) {
                return fetchCollection(fieldNumber, mmd);
            } else if (mmd.hasMap()) {
                return null;
            }
        }
        String attrName = mmd.getColumnMetaData()[0].getName();
        return DNValues.getSingleValue(_object, attrName);
    }

    protected Object fetchCollection(final int fieldNumber,
            final AbstractMemberMetaData mmd) {
        AbstractClassMetaData cmd = DNMetaData.getCollectionElementMetadata(ec,
                mmd);
        Class type = mmd.getType();
        List<String> values = fetchRepeatingField(fieldNumber,
                IDfValue.DF_STRING);
        if (cmd == null) {
            if (mmd.hasCollection()) {
                return values;
            }
            return Nucleus.newArray(type, values);
        }
        return null;
    }

}
