package pro.documentum.util.crypto;

import javax.crypto.SecretKey;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class LTKUtil extends AbstractKey {

    private final LTK _ltk;

    private final SecretKey _key;

    private LTKUtil(final LTK ltk, final SecretKey key) {
        _ltk = ltk;
        _key = key;
    }

    public static LTKUtil getInstance(final LTK ltk, final SecretKey secretKey) {
        Header header = ltk.getHeader();
        Algorithm algorithm = Algorithm.of(header.getAlgorithm());
        IVector vi = ltk.getIV();
        byte[] vector = DBK_3DES_IV;
        if (vi != null) {
            vector = vi.getVector();
        }
        byte[] decrypted = decrypt(ltk.getEncrypted(), secretKey, vector,
                algorithm.getCipher());
        byte[] key = new byte[ltk.getHeader().getKeyLength()];
        int offset = decrypted.length - key.length;
        System.arraycopy(decrypted, offset, key, 0, key.length);
        return new LTKUtil(ltk, algorithm.createSecretKey(key));
    }

    public static LTKUtil getInstance(final byte[] bytes, final DBKUtil dbkUtil) {
        return getInstance(LTK.of(bytes), dbkUtil.getKey());
    }

    public static LTKUtil getInstance(final byte[] bytes,
            final SecretKey secretKey) {
        return getInstance(LTK.of(bytes), secretKey);
    }

    public DBK getDBK() {
        return _ltk;
    }

    public SecretKey getKey() {
        return _key;
    }

}
