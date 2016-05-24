package pro.documentum.persistence.common.util;

import java.util.Arrays;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.state.ObjectProvider;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNArrays {

    private DNArrays() {
        super();
    }

    private static int[] filter(final int[] current, final int[] required) {
        int[] result = new int[current.length];
        if (required == null) {
            return new int[0];
        }
        int i = 0;
        for (int fieldNumber : current) {
            boolean isRequired = false;
            for (int rf : required) {
                if (rf == fieldNumber) {
                    isRequired = true;
                    break;
                }
            }
            if (isRequired) {
                result[i] = fieldNumber;
                i++;
            }
        }
        return Arrays.copyOf(result, i);
    }

    public static int[] getDirtyFields(final ObjectProvider<?> op,
            final int[] ints) {
        return filter(ints, op.getDirtyFieldNumbers());
    }

    public static int[] getPersistentFields(final AbstractClassMetaData cmd,
            final int[] ints) {
        int[] persistent = new int[ints.length];
        int i = 0;
        for (int fieldNumber : ints) {
            AbstractMemberMetaData mmd = cmd
                    .getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            if (mmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
                persistent[i] = fieldNumber;
                i++;
            }
        }
        return Arrays.copyOf(persistent, i);
    }

    public static int[] getNonPersistentFields(final AbstractClassMetaData cmd,
            final int[] ints) {
        int[] nonPersistent = new int[ints.length];
        int i = 0;
        for (int fieldNumber : ints) {
            AbstractMemberMetaData mmd = cmd
                    .getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            if (mmd.getPersistenceModifier() != FieldPersistenceModifier.PERSISTENT) {
                nonPersistent[i] = fieldNumber;
                i++;
            }
        }
        return Arrays.copyOf(nonPersistent, i);
    }

}
