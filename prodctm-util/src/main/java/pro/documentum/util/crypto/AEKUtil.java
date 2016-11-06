package pro.documentum.util.crypto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.SecretKey;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.common.DfCriticalException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class AEKUtil extends AbstractKey {

    private final AEK _aek;

    private final SecretKey _key;

    private AEKUtil(final AEK aek, final SecretKey key) {
        _aek = aek;
        _key = key;
    }

    public static AEKUtil getInstance(final String location) {
        return getInstance(location, getDefaultPassphrase());
    }

    public static AEKUtil getInstance(final String location,
            final String passphrase) {
        return getInstance(getBytes(location), passphrase);
    }

    public static AEKUtil getInstance(final byte[] bytes) {
        return getInstance(bytes, getDefaultPassphrase());
    }

    public static AEKUtil getInstance(final byte[] bytes,
            final String passphrase) {
        AEK aek = AEK.of(bytes);

        Header header = aek.getHeader();

        CipherInfo cipherInfo = aek.getCipherInfo();
        byte[] salt = cipherInfo.getSalt();

        IVector iv = aek.getIV();

        Algorithm algorithm = Algorithm.of(header.getAlgorithm());

        char[] password = Objects.requireNonNull(passphrase).toCharArray();
        SecretKey tmpKey = algorithm.createSecretKey(password, salt);

        byte[] vector = AEK_3DES_IV;
        if (iv != null) {
            vector = iv.getVector();
        }

        byte[] encrypted = aek.getEncrypted();
        byte[] decrypted = decrypt(encrypted, tmpKey, vector,
                algorithm.getCipher());

        byte[] decryptedSalt = new byte[salt.length];
        System.arraycopy(decrypted, 0, decryptedSalt, 0, salt.length);
        if (!Arrays.equals(decryptedSalt, salt)) {
            throw new DfCriticalException("Salts do not match");
        }

        byte[] key = new byte[decrypted.length - salt.length];
        System.arraycopy(decrypted, salt.length, key, 0, key.length);
        return new AEKUtil(aek, algorithm.createSecretKey(key));
    }

    private static String getDefaultPassphrase() {
        return "p6lo3ly1oj5ne&";
    }

    private static byte[] getBytes(final String location) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(location));
            ByteArrayOutputStream bais = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, bais);
            return bais.toByteArray();
        } catch (IOException ex) {
            throw new DfCriticalException(ex);
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    public AEK getAEK() {
        return _aek;
    }

    public SecretKey getKey() {
        return _key;
    }

    public byte[] getIV() {
        byte[] vector = AEK_3DES_IV;
        IVector iv = _aek.getIV();
        if (iv != null) {
            vector = iv.getVector();
        }
        return vector;
    }

}
