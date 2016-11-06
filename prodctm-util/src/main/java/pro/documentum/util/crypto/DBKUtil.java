package pro.documentum.util.crypto;

import javax.crypto.SecretKey;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DBKUtil extends AbstractKey {

    private final DBK _dbk;

    private final SecretKey _key;

    private DBKUtil(final DBK dbk, final SecretKey key) {
        _dbk = dbk;
        _key = key;
    }

    public static DBKUtil getInstance(final DBK dbk, final SecretKey secretKey) {
        Header header = dbk.getHeader();
        Algorithm algorithm = Algorithm.of(header.getAlgorithm());
        IVector vi = dbk.getIV();
        byte[] vector = DBK_3DES_IV;
        if (vi != null) {
            vector = vi.getVector();
        }
        byte[] decrypted = decrypt(dbk.getEncrypted(), secretKey, vector,
                algorithm.getCipher());
        byte[] key = new byte[dbk.getHeader().getKeyLength()];
        int offset = decrypted.length - key.length;
        System.arraycopy(decrypted, offset, key, 0, key.length);
        return new DBKUtil(dbk, algorithm.createSecretKey(key));
    }

    public static DBKUtil getInstance(final byte[] bytes, final AEKUtil aekUtil) {
        return getInstance(DBK.of(bytes), aekUtil.getKey());
    }

    public static DBKUtil getInstance(final byte[] bytes,
            final SecretKey secretKey) {
        return getInstance(DBK.of(bytes), secretKey);
    }

    public DBK getDBK() {
        return _dbk;
    }

    public SecretKey getKey() {
        return _key;
    }

}
