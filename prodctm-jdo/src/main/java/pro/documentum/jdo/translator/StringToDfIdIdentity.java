package pro.documentum.jdo.translator;

import org.datanucleus.ExecutionContext;
import org.datanucleus.identity.IdentityManager;
import org.datanucleus.identity.IdentityStringTranslator;

import pro.documentum.util.ids.DfIdUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class StringToDfIdIdentity implements IdentityStringTranslator {

    private static final long serialVersionUID = -7554656133749293146L;

    public StringToDfIdIdentity() {
        super();
    }

    @Override
    public Object getIdentity(final ExecutionContext ec, final String stringId) {
        if (DfIdUtil.isNotObjectId(stringId)) {
            return stringId;
        }
        IdentityManager im = ec.getNucleusContext().getIdentityManager();
        return im.getDatastoreId(null, stringId);
    }

}
