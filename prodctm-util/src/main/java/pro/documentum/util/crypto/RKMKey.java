package pro.documentum.util.crypto;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class RKMKey extends AbstractKeyData {

    @KeyField(order = 0)
    private int _location;

    @KeyField(order = 1)
    private int _type;

    private RKMKey(final boolean swap) {
        super(swap);
    }

    public static RKMKey of(final byte[] buffer, final int offset,
            final boolean swap) {
        RKMKey key = new RKMKey(swap);
        key.load(buffer, offset);
        return key;
    }

    public int getLocation() {
        return _location;
    }

    public void setLocation(final int location) {
        _location = location;
    }

    public int getType() {
        return _type;
    }

    public void setType(final int type) {
        _type = type;
    }

}
