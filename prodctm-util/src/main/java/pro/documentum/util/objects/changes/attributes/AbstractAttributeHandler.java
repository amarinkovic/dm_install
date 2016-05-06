package pro.documentum.util.objects.changes.attributes;

import java.lang.annotation.Annotation;
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
        if (_dependencies != null) {
            return Collections.unmodifiableList(_dependencies);
        }
        _dependencies = buildDependencies(getClass());
        return _dependencies;
    }

    private List<Class<? extends IAttributeHandler>> buildDependencies(
            final Class cls) {
        List<Class<? extends IAttributeHandler>> result = new ArrayList<Class<? extends IAttributeHandler>>();
        Class current = cls;
        while (current != Object.class) {
            Annotation annotation = cls.getAnnotation(Depends.class);
            if (annotation != null) {
                for (Class<? extends IAttributeHandler> dep : ((Depends) annotation)
                        .on()) {
                    if (result.contains(dep)) {
                        continue;
                    }
                    result.add(dep);
                }
            }
            current = current.getSuperclass();
        }
        return result;
    }

}
