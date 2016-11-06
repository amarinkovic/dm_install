package pro.documentum.util.crypto;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import com.documentum.fc.common.DfCriticalException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractKeyData {

    public static final int INTEGER_LENGTH = 4;

    private final boolean _swap;

    public AbstractKeyData(final boolean swap) {
        _swap = swap;
    }

    public static int readInteger(final byte[] buffer, final int offset,
            final boolean swap) {
        byte[] data = readBytes(buffer, offset, INTEGER_LENGTH);
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            int shift = ((INTEGER_LENGTH - 1) * 8) - 8 * i;
            if (swap) {
                shift = ((INTEGER_LENGTH - 1) * 8) - shift;
            }
            result += (data[i] & 255) << shift;
        }
        return result;
    }

    public static byte[] readBytes(final byte[] buffer, final int offset,
            final int length) {
        byte[] result = new byte[length];
        System.arraycopy(buffer, offset, result, 0, length);
        return result;
    }

    public boolean isSwap() {
        return _swap;
    }

    public int getDataSize() {
        try {
            int result = 0;
            for (Field field : getAEKFields()) {
                Class<?> fieldType = field.getType();
                if (fieldType == int.class || fieldType == Integer.class) {
                    result += INTEGER_LENGTH;
                } else if (fieldType == byte[].class) {
                    result += Array.getLength(field.get(this));
                }
            }
            return result;
        } catch (IllegalAccessException ex) {
            throw new DfCriticalException(ex);
        }
    }

    protected void load(final byte[] buffer, final int offset) {
        try {
            int shift = offset;
            for (Field field : getAEKFields()) {
                if (field == null) {
                    continue;
                }
                KeyField keyField = field.getAnnotation(KeyField.class);
                shift = readField(buffer, shift, field, keyField);
            }
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new DfCriticalException(ex);
        }
    }

    private Field[] getAEKFields() {
        Field[] fields = getClass().getDeclaredFields();
        Field[] result = new Field[fields.length];
        for (Field field : getClass().getDeclaredFields()) {
            KeyField keyField = field.getAnnotation(KeyField.class);
            if (keyField == null) {
                continue;
            }
            field.setAccessible(true);
            result[keyField.order()] = field;
        }
        return result;
    }

    protected int readField(final byte[] buffer, final int offset,
            final Field field, final KeyField keyField)
        throws IllegalAccessException, NoSuchFieldException {
        Class<?> fieldType = field.getType();
        if (fieldType == int.class || fieldType == Integer.class) {
            return readIntegerField(buffer, offset, field, keyField);
        } else if (fieldType == byte[].class) {
            return readByteField(buffer, offset, field, keyField);
        }
        throw new DfCriticalException("Field " + field.getName()
                + " ha invalid type");
    }

    protected int readIntegerField(final byte[] buffer, final int offset,
            final Field field, final KeyField keyField)
        throws IllegalAccessException, NoSuchFieldException {
        field.set(this, readInteger(buffer, offset, isSwap()));
        return offset + INTEGER_LENGTH;
    }

    protected int readByteField(final byte[] buffer, final int offset,
            final Field field, final KeyField keyField)
        throws IllegalAccessException, NoSuchFieldException {
        Field lengthFiled = getClass().getDeclaredField(keyField.lengthField());
        lengthFiled.setAccessible(true);
        byte[] data = readBytes(buffer, offset, (Integer) lengthFiled.get(this));
        field.set(this, data);
        return offset + data.length;
    }

}
