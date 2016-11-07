package pro.documentum.util.crypto;

import com.documentum.fc.common.DfCriticalException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Header extends AbstractKeyData {

    @KeyField(order = 0, min = 0, max = 2)
    private int _version;

    @KeyField(order = 1, min = 1, max = 1024)
    private int _size;

    @KeyField(order = 2, min = 3, max = 4)
    private int _storageType;

    @KeyField(order = 3)
    private int _encryptedLength;

    @KeyField(order = 4, min = 1, max = 4)
    private int _algorithm;

    @KeyField(order = 5)
    private int _keyLength;

    @KeyField(order = 6)
    private int _blockSize;

    private Header(final byte[] buffer, final int offset) {
        super(readSwapped(buffer, offset));
    }

    public static Header of(final byte[] buffer, final int offset) {
        Header header = new Header(buffer, offset);
        header.load(buffer, offset);
        return header;
    }

    private static boolean readSwapped(final byte[] buffer, final int offset) {
        int version = readInteger(buffer, offset, false);
        if (version >= 0 && version <= 2) {
            return false;
        }
        version = readInteger(buffer, offset, true);
        if (version < 0 && version > 2) {
            throw new DfCriticalException("Unable to read version");
        }
        return true;
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

    public int getStorageType() {
        return _storageType;
    }

    public void setStorageType(final int storageType) {
        _storageType = storageType;
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

}
