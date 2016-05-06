package pro.documentum.util.objects.changes.attributes.persistent;

import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import pro.documentum.util.objects.changes.attributes.AbstractAttributeHandler;
import pro.documentum.util.objects.changes.attributes.Depends;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Depends(on = {AspectNameHandler.class })
public abstract class AbstractPersistentAttributeHandler<T extends IDfPersistentObject>
        extends AbstractAttributeHandler<T> {

    @Override
    public boolean accept(final Object object, final Set<String> attrNames)
        throws DfException {
        if (object instanceof IDfPersistentObject) {
            return doAccept(attrNames);
        }
        return false;
    }

    protected boolean requireTransaction() {
        return false;
    }

    @Override
    public boolean apply(final T object, final Map<String, ?> values)
        throws DfException {
        // means that values contains expected attributes
        // and we don't need to perform extra checks
        if (!doAccept(values.keySet())) {
            return false;
        }
        if (requireTransaction()) {
            checkTransaction(object);
        }
        boolean result = doApply(object, values);
        if (!result) {
            checkState(values);
        }
        return result;
    }

    protected abstract boolean doApply(T object, Map<String, ?> values)
        throws DfException;

    protected abstract boolean doAccept(final Set<String> attrNames);

    private void checkState(final Map<String, ?> values) {
        if (doAccept(values.keySet())) {
            throw new IllegalStateException(
                    "Object handler must delete attributes");
        }
    }

    private void checkTransaction(final T object) throws DfException {
        IDfSession session = object.getObjectSession();
        if (!session.isTransactionActive()) {
            throw new IllegalStateException(
                    "Operation must be performed in transaction");
        }
    }

}
