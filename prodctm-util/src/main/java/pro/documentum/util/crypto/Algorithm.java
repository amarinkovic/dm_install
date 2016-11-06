package pro.documentum.util.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.documentum.fc.common.DfCriticalException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public enum Algorithm {

    DES_EDE3(1, "PBKDF2WithHmacSHA1", "DESede", "DESede/CBC/PKCS5Padding", 192),

    AES_128(2, "PBKDF2WithHmacSHA1", "AES", "AES/CBC/PKCS5Padding", 128),

    AES_256(3, "PBKDF2WithHmacSHA1", "AES", "AES/CBC/PKCS5Padding", 256),

    AES_192(4, "PBKDF2WithHmacSHA1", "AES", "AES/CBC/PKCS5Padding", 192);

    private int _id;

    private String _factory;

    private String _type;

    private String _cipher;

    private int _length;

    Algorithm(final int id, final String factory, final String type,
            final String cipher, final int length) {
        _id = id;
        _factory = factory;
        _type = type;
        _cipher = cipher;
        _length = length;
    }

    public static Algorithm of(final int algorithmId) {
        for (Algorithm algorithm : Algorithm.values()) {
            if (algorithmId == algorithm.getId()) {
                return algorithm;
            }
        }
        throw new DfCriticalException("Unsupported algorithm: " + algorithmId);
    }

    public int getId() {
        return _id;
    }

    public String getFactory() {
        return _factory;
    }

    public String getType() {
        return _type;
    }

    public String getCipher() {
        return _cipher;
    }

    public int getLength() {
        return _length;
    }

    public SecretKey createSecretKey(final byte[] key) {
        return new SecretKeySpec(key, getType());
    }

    public SecretKey createSecretKey(final char[] password, final byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory
                    .getInstance(getFactory());
            PBEKeySpec spec = new PBEKeySpec(password, salt, 1024, getLength());
            SecretKey key = factory.generateSecret(spec);
            return new SecretKeySpec(key.getEncoded(), getType());
        } catch (GeneralSecurityException ex) {
            throw new DfCriticalException(ex);
        }
    }

}
