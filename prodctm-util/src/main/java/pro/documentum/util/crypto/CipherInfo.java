package pro.documentum.util.crypto;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class CipherInfo extends AbstractKeyData {

    @KeyField(order = 0)
    private int _version;

    @KeyField(order = 1)
    private int _size;

    @KeyField(order = 2)
    private int _encryptedLength;

    @KeyField(order = 3)
    private int _algorithm;

    @KeyField(order = 4)
    private int _keyLength;

    @KeyField(order = 5)
    private int _blockSize;

    @KeyField(order = 6)
    private int _iterationCount;

    @KeyField(order = 7)
    private int _saltLength;

    @KeyField(order = 8, lengthField = "_saltLength")
    private byte[] _salt;

    private CipherInfo(final boolean swap) {
        super(swap);
    }

    public static CipherInfo of(final byte[] buffer, final int offset,
            final boolean swap) {
        CipherInfo cipherInfo = new CipherInfo(swap);
        cipherInfo.load(buffer, offset);
        return cipherInfo;
    }

    public int getVersion() {
        return _version;
    }

    public void setVersion(final int version) {
        _version = version;
    }

    public int getSize() {
        return _size;
    }

    public void setSize(final int size) {
        _size = size;
    }

    public int getEncryptedLength() {
        return _encryptedLength;
    }

    public void setEncryptedLength(final int encryptedLength) {
        _encryptedLength = encryptedLength;
    }

    public int getAlgorithm() {
        return _algorithm;
    }

    public void setAlgorithm(final int algorithm) {
        _algorithm = algorithm;
    }

    public int getKeyLength() {
        return _keyLength;
    }

    public void setKeyLength(final int keyLength) {
        _keyLength = keyLength;
    }

    public int getBlockSize() {
        return _blockSize;
    }

    public void setBlockSize(final int blockSize) {
        _blockSize = blockSize;
    }

    public int getIterationCount() {
        return _iterationCount;
    }

    public void setIterationCount(final int iterationCount) {
        _iterationCount = iterationCount;
    }

    public int getSaltLength() {
        return _saltLength;
    }

    public void setSaltLength(final int saltLength) {
        _saltLength = saltLength;
    }

    public byte[] getSalt() {
        return _salt;
    }

    public void setSalt(final byte[] salt) {
        _salt = salt;
    }

}
