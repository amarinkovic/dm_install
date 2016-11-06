package pro.documentum.util.crypto;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AEK {

    private final Header _header;

    private final CipherInfo _cipherInfo;

    private final RKMKey _rkmKey;

    private final IVector _iv;

    private byte[] _encrypted;

    public AEK(final Header header, final CipherInfo cipherInfo,
            final RKMKey rkmKey, final IVector iv, final byte[] encrypted) {
        _header = header;
        _cipherInfo = cipherInfo;
        _rkmKey = rkmKey;
        _iv = iv;
        _encrypted = encrypted;
    }

    public static AEK of(final byte[] data) {
        Header header = Header.of(data, 0);
        RKMKey rkmKey = null;
        IVector iVector = null;
        int offset = header.getDataSize();
        boolean swap = header.isSwap();

        if (header.getStorageType() == 4) {
            Docbase docbase = Docbase.of(data, offset, swap);
            offset += docbase.getDataSize();
        }

        if (header.getVersion() > 1) {
            rkmKey = RKMKey.of(data, offset, false);
            offset += rkmKey.getDataSize();
            iVector = IVector.of(data, offset, false);
            offset += iVector.getDataSize();
        }

        CipherInfo cipherInfo = CipherInfo.of(data, offset, swap);
        offset += cipherInfo.getDataSize();

        int dataLength = cipherInfo.getEncryptedLength();

        byte[] encrypted = AbstractKeyData.readBytes(data, offset, dataLength);

        return new AEK(header, cipherInfo, rkmKey, iVector, encrypted);
    }

    public Header getHeader() {
        return _header;
    }

    public CipherInfo getCipherInfo() {
        return _cipherInfo;
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

}
