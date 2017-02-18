package pro.documentum.persistence.common.util.fields;

import java.util.Arrays;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class ArrayFieldFilter implements IFieldFilter {

    private final int[] _required;

    private ArrayFieldFilter(final int[] required) {
        _required = new int[required.length];
        System.arraycopy(required, 0, _required, 0, required.length);
        Arrays.sort(_required);
    }

    public static IFieldFilter getInstance(final int[] required) {
        return new ArrayFieldFilter(required);
    }

    @Override
    public boolean accept(final int fieldNumber) {
        return Arrays.binarySearch(_required, fieldNumber) >= 0;
    }

}
