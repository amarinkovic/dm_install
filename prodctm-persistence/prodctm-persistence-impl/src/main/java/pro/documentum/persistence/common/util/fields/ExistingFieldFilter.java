package pro.documentum.persistence.common.util.fields;

import java.util.Objects;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;

import com.documentum.fc.client.IDfTypedObject;

import pro.documentum.persistence.common.util.DNValues;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class ExistingFieldFilter implements IFieldFilter {

    private final AbstractClassMetaData _cmd;

    private final IDfTypedObject _object;

    private ExistingFieldFilter(final AbstractClassMetaData cmd,
            final IDfTypedObject object) {
        _cmd = Objects.requireNonNull(cmd);
        _object = Objects.requireNonNull(object);
    }

    public static IFieldFilter getInstance(final AbstractClassMetaData cmd,
            final IDfTypedObject object) {
        return new ExistingFieldFilter(cmd, object);
    }

    @Override
    public boolean accept(final int fieldNumber) {
        AbstractMemberMetaData mmd = _cmd
                .getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        for (ColumnMetaData md : mmd.getColumnMetaData()) {
            String column = md.getName();
            if (!DNValues.hasAttr(_object, column)) {
                return false;
            }
        }
        return true;
    }

}
