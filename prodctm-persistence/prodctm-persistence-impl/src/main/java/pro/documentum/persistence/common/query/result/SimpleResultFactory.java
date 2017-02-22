package pro.documentum.persistence.common.query.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.datanucleus.ExecutionContext;

import com.documentum.fc.client.IDfTypedObject;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class SimpleResultFactory<E> implements IResultFactory<E> {

    private final Class _resultClass;

    public SimpleResultFactory(final ExecutionContext ec,
            final List<String> columns, final Class resultClass) {
        _resultClass = getResultClass(columns, resultClass);
    }

    private Class getResultClass(final List<String> columns,
            final Class resultClass) {
        Class cls = resultClass;
        if (cls == null) {
            if (columns.size() > 1) {
                cls = Object[].class;
            } else {
                cls = Object.class;
            }
        } else if (Map.class.equals(resultClass)) {
            return HashMap.class;
        }
        return cls;
    }

    @Override
    public E getObject(final IDfTypedObject object) {
        return (E) object;
    }

}
