package pro.documentum.util.objects.changes.attributes;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IAttributeHandler<T extends IDfPersistentObject> {

    List<Class<? extends IAttributeHandler>> getDependencies();

    boolean apply(T object, Map<String, ?> values) throws DfException;

    boolean accept(Object object, Set<String> attrNames) throws DfException;

}
