package pro.documentum.util.crypto;

import static junit.framework.TestCase.assertNotNull;

import org.apache.commons.codec.binary.Base64;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DBKUtilTest {

    public static final String AES256_CRYPTO_KEY = "AgAAACAAAAAEAAAAMAAAAAMAAAAgAAAAEAAAAOlLAgAAAAAAAAAAAQAAABCQ3VXFXcetsIJMRmNPqVh+keZ6OJGpX74wr4HJhLAYJeHPC9Z6z+L2qUHH4BpYPyW1P3cC2MaUu9+Q1Rcnub4w";

    public static final String DES3_CRYPTO_KEY = "AQAAACAAAAAEAAAAIAAAAAEAAAAYAAAAEAAAAEharwCi1YLI9lk0t8tIxYOb9Ibnx24CInyqKP5EAB8Cfq2Nlg==";

    @Test
    @Ignore("requires java unrestricted policy files")
    public void testAES256() throws Exception {
        AEKUtil aekUtil = AEKUtil.getInstance(AEKUtilTest.AES256);
        assertNotNull(aekUtil.getKey());
        DBKUtil dbkUtil = DBKUtil.getInstance(
                Base64.decodeBase64(AES256_CRYPTO_KEY.getBytes()),
                aekUtil.getKey());
        assertNotNull(dbkUtil.getKey());
    }

    @Test
    public void test3DES() throws Exception {
        AEKUtil aekUtil = AEKUtil.getInstance(AEKUtilTest.DES3);
        assertNotNull(aekUtil.getKey());
        DBKUtil dbkUtil = DBKUtil.getInstance(
                Base64.decodeBase64(DES3_CRYPTO_KEY.getBytes()),
                aekUtil.getKey());
        assertNotNull(dbkUtil.getKey());
    }

}
