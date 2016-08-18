package pro.documentum.persistence.common.util.fields;

import java.util.Objects;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class PersistentFieldFilter implements IFieldFilter {

    private final AbstractClassMetaData _cmd;

    private PersistentFieldFilter(final AbstractClassMetaData cmd) {
        _cmd = Objects.requireNonNull(cmd);
    }

    public static IFieldFilter getInstance(final AbstractClassMetaData cmd) {
        return new PersistentFieldFilter(cmd);
    }

    @Override
    public boolean accept(final int fieldNumber) {
        AbstractMemberMetaData mmd = _cmd
                .getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        return mmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT;
    }

}
