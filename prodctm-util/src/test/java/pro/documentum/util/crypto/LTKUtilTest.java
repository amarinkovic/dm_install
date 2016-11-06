package pro.documentum.util.crypto;

import static junit.framework.TestCase.assertNotNull;

import org.apache.commons.codec.binary.Base64;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class LTKUtilTest {

    public static final String AES256_TICKET_CRYPTO_KEY = "AgAAACAAAAAEAAAAMAAAAAMAAAAgAAAAEAAAAOlLAgAAAAAAAAAAAgAAABDs2DMzoV1sNOR1Mew9hBQ4bn+z/NTzD9XmZ5CbVeLOzFLgA+tiXbg8w32WDzFzQj6ZLE4X6F+cgRPy4sIt4U28";

    public static final String DES3_TICKET_CRYPTO_KEY = "AQAAACAAAAAEAAAAIAAAAAEAAAAYAAAAEAAAAEharwB00LKv1AhNlmMmZBB8E/DBSoxJgzz6lmEoo7ek8pp4mA==";

    @Test
    @Ignore("requires java unrestricted policy files")
    public void testAES256() throws Exception {
        AEKUtil aekUtil = AEKUtil.getInstance(AEKUtilTest.AES256);
        assertNotNull(aekUtil.getKey());
        DBKUtil dbkUtil = DBKUtil.getInstance(
                Base64.decodeBase64(DBKUtilTest.AES256_CRYPTO_KEY.getBytes()),
                aekUtil.getKey());
        assertNotNull(dbkUtil.getKey());
        LTKUtil ltkUtil = LTKUtil.getInstance(
                Base64.decodeBase64(AES256_TICKET_CRYPTO_KEY.getBytes()),
                dbkUtil.getKey());
        assertNotNull(ltkUtil.getKey());
    }

    @Test
    public void test3DES() throws Exception {
        AEKUtil aekUtil = AEKUtil.getInstance(AEKUtilTest.DES3);
        assertNotNull(aekUtil.getKey());
        DBKUtil dbkUtil = DBKUtil.getInstance(
                Base64.decodeBase64(DBKUtilTest.DES3_CRYPTO_KEY.getBytes()),
                aekUtil.getKey());
        assertNotNull(dbkUtil.getKey());
        LTKUtil ltkUtil = LTKUtil.getInstance(
                Base64.decodeBase64(DES3_TICKET_CRYPTO_KEY.getBytes()),
                dbkUtil.getKey());
        assertNotNull(ltkUtil.getKey());
    }

}
