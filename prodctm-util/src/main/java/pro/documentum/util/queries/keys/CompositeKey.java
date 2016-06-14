package pro.documentum.util.queries.keys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.documentum.fc.common.DfDocbaseConstants;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class CompositeKey {

    private final Map<String, Object> _mapping = new HashMap<>();

    private final List<String> _attributes = new ArrayList<>();

    public CompositeKey() {
        super();
    }

    public boolean isIdentity() {
        return isOnlyAttr(DfDocbaseConstants.R_OBJECT_ID);
    }

    public boolean isOnlyAttr(final String attrName) {
        return _attributes.size() == 1
                && Objects.equals(attrName, getFirstAttr());
    }

    public String getFirstAttr() {
        if (_attributes.size() > 0) {
            return _attributes.get(0);
        }
        return null;
    }

    public List<String> getKeys() {
        return Collections.unmodifiableList(_attributes);
    }

    public CompositeKey add(final String column, final Object value) {
        doAdd(column, value);
        return this;
    }

    protected final void doAdd(final String column, final Object value) {
        _mapping.put(column, value);
        if (!_attributes.contains(column)) {
            _attributes.add(column);
        }
    }

    public Map<String, Object> getMapping() {
        return Collections.unmodifiableMap(_mapping);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompositeKey)) {
            return false;
        }
        CompositeKey key = (CompositeKey) o;
        return Objects.equals(_mapping, key._mapping);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_mapping);
    }

}
