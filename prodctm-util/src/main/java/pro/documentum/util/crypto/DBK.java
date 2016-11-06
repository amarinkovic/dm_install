package pro.documentum.util.crypto;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DBK {

    private final Header _header;

    private final RKMKey _rkmKey;

    private final IVector _iv;

    private byte[] _encrypted;

    public DBK(final Header header, final RKMKey rkmKey, final IVector iv,
            final byte[] encrypted) {
        _header = header;
        _rkmKey = rkmKey;
        _iv = iv;
        _encrypted = encrypted;
    }

    public static DBK of(final byte[] data) {
        Header header = Header.of(data, 0);
        RKMKey rkmKey = null;
        IVector ivector = null;
        int offset = header.getDataSize();
        boolean swap = header.isSwap();

        Docbase docbase = Docbase.of(data, offset, swap);
        offset += docbase.getDataSize();

        if (header.getVersion() > 1) {
            rkmKey = RKMKey.of(data, offset, false);
            offset += rkmKey.getDataSize();
            ivector = IVector.of(data, offset, false);
            offset += ivector.getDataSize();
        }

        int dataLength = header.getEncryptedLength();
        byte[] encrypted = AbstractKeyData.readBytes(data, offset, dataLength);
        return new DBK(header, rkmKey, ivector, encrypted);
    }

    public Header getHeader() {
        return _header;
    }

    public RKMKey getRKMKey() {
        return _rkmKey;
    }

    public IVector getIV() {
        return _iv;
    }

    public byte[] getEncrypted() {
        return _encrypted;
    }

    public void setEncrypted(final byte[] encrypted) {
        _encrypted = encrypted;
    }

}
