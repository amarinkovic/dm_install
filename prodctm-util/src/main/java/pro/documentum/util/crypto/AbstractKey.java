package pro.documentum.util.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.documentum.fc.common.DfCriticalException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractKey {

    protected static final byte[] AEK_3DES_IV = {0x17, 0x7d, 0x65, (byte) 0xb6,
        0x70, (byte) 0xf9, (byte) 0xe2, (byte) 0xee, };

    protected static final byte[] DBK_3DES_IV = {0x76, 0x10, 0x5b, 0x60, 0x1b,
        (byte) 0xf0, 0x54, (byte) 0xc6, };

    protected static byte[] decrypt(final byte[] data, final SecretKey key,
            final byte[] iv, final String cipherName) {
        try {
            Cipher cipher = Cipher.getInstance(cipherName);
            IvParameterSpec parameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
            return cipher.doFinal(data);
        } catch (GeneralSecurityException ex) {
            throw new DfCriticalException(ex);
        }
    }

}
