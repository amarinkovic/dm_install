package pro.documentum.util.objects.changes.attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.documentum.fc.client.IDfPersistentObject;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractAttributeHandler<T extends IDfPersistentObject>
        implements IAttributeHandler<T> {

    private List<Class<? extends IAttributeHandler>> _dependencies;

    @Override
    public List<Class<? extends IAttributeHandler>> getDependencies() {
        if (isReadOnly()) {
            return Collections.emptyList();
        }
        if (_dependencies != null) {
            return Collections.unmodifiableList(_dependencies);
        }
        _dependencies = buildDependencies(getClass());
        _dependencies.remove(getClass());
        return _dependencies;
    }

    private List<Class<? extends IAttributeHandler>> buildDependencies(
            final Class cls) {
        List<Class<? extends IAttributeHandler>> result = new ArrayList<Class<? extends IAttributeHandler>>();
        Class current = null;
        do {
            if (current == null) {
                current = cls;
            } else {
                current = current.getSuperclass();
            }
            Depends depends = (Depends) current.getAnnotation(Depends.class);
            if (depends == null) {
                continue;
            }
            for (Class<? extends IAttributeHandler> dep : depends.on()) {
                if (result.contains(dep)) {
                    continue;
                }
                result.add(dep);
            }
        } while (current != Object.class);
        return result;
    }

    protected boolean isReadOnly() {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getName();
    }

}
