package pro.documentum.util.crypto;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class IVector extends AbstractKeyData {

    @KeyField(order = 0)
    private int _length;

    @KeyField(order = 1, lengthField = "_length")
    private byte[] _vector;

    private IVector(final boolean swap) {
        super(swap);
    }

    public static IVector of(final byte[] buffer, final int offset,
            final boolean swap) {
        IVector iv = new IVector(swap);
        iv.load(buffer, offset);
        return iv;
    }

    public int getLength() {
        return _length;
    }

    public void setLength(final int length) {
        _length = length;
    }

    public byte[] getVector() {
        return _vector;
    }

    public void setVector(final byte[] vector) {
        _vector = vector;
    }

}
