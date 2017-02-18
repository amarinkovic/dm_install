package pro.documentum.persistence.common.util;

import java.util.Arrays;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.ObjectProvider;

import com.documentum.fc.client.IDfTypedObject;

import pro.documentum.persistence.common.util.fields.ArrayFieldFilter;
import pro.documentum.persistence.common.util.fields.ExistingFieldFilter;
import pro.documentum.persistence.common.util.fields.IFieldFilter;
import pro.documentum.persistence.common.util.fields.NonPersistentFieldFilter;
import pro.documentum.persistence.common.util.fields.PersistentFieldFilter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNFields {

    private DNFields() {
        super();
    }

    private static int[] filter(final int[] current, final int[] required) {
        if (required == null) {
            return new int[0];
        }
        return filterOut(current, ArrayFieldFilter.getInstance(required));
    }

    public static int[] getDirtyFields(final ObjectProvider<?> op,
            final int[] ints) {
        return filter(ints, op.getDirtyFieldNumbers());
    }

    public static int[] getPersistentFields(final AbstractClassMetaData cmd,
            final int[] candidates) {
        return filterOut(candidates, PersistentFieldFilter.getInstance(cmd));
    }

    public static int[] getNonPersistentFields(final AbstractClassMetaData cmd,
            final int[] candidates) {
        return filterOut(candidates, NonPersistentFieldFilter.getInstance(cmd));
    }

    public static int[] getPresentMembers(final int[] members,
            final AbstractClassMetaData cmd, final IDfTypedObject cursor) {
        return filterOut(members, ExistingFieldFilter.getInstance(cmd, cursor));
    }

    private static int[] filterOut(final int[] candidates,
            final IFieldFilter fieldFilter) {
        int[] persistent = new int[candidates.length];
        int i = 0;
        for (int fieldNumber : candidates) {
            if (!fieldFilter.accept(fieldNumber)) {
                continue;
            }
            persistent[i] = fieldNumber;
            i++;
        }
        return Arrays.copyOf(persistent, i);
    }

}
