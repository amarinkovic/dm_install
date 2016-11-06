package pro.documentum.util.crypto;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Docbase extends AbstractKeyData {

    @KeyField(order = 0, min = 0, max = 2)
    private int _docbaseId;

    private Docbase(final boolean swap) {
        super(swap);
    }

    public static Docbase of(final byte[] buffer, final int offset,
            final boolean swap) {
        Docbase docbase = new Docbase(swap);
        docbase.load(buffer, offset);
        return docbase;
    }

    public int getDocbaseId() {
        return _docbaseId;
    }

    public void setDocbaseId(final int docbaseId) {
        _docbaseId = docbaseId;
    }

}
