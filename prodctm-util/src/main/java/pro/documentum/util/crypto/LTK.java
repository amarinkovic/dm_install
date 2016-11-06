package pro.documentum.util.crypto;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class LTK extends DBK {

    public LTK(final Header header, final RKMKey rkmKey, final IVector iv,
            final byte[] encrypted) {
        super(header, rkmKey, iv, encrypted);
    }

    public static LTK of(final byte[] data) {
        Header header = Header.of(data, 0);
        RKMKey rkmKey = null;
        IVector ivector = null;
        int offset = header.getDataSize();
        boolean swap = header.isSwap();

        Docbase docbase = Docbase.of(data, offset, swap);
        offset += docbase.getDataSize();

        if (header.getVersion() > 1) {
            rkmKey = RKMKey.of(data, offset, false);
            offset += rkmKey.getDataSize();
            ivector = IVector.of(data, offset, false);
            offset += ivector.getDataSize();
        }

        int dataLength = header.getEncryptedLength();
        byte[] encrypted = AbstractKeyData.readBytes(data, offset, dataLength);
        return new LTK(header, rkmKey, ivector, encrypted);
    }

}
