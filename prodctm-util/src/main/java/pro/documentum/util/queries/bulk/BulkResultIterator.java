package pro.documentum.util.queries.bulk;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class BulkResultIterator<K, V> implements Iterator<V> {

    private final List<K> _keys;

    private final Map<K, List<V>> _objects;

    private int _position = -1;

    private List<V> _current;

    BulkResultIterator(final List<K> keys, final Map<K, List<V>> objects) {
        _keys = keys;
        _objects = objects;
    }

    @Override
    public boolean hasNext() {
        if (_keys.size() > _position + 1) {
            return true;
        }
        return _current != null && !_current.isEmpty();
    }

    @Override
    public V next() {
        if (_current != null) {
            return removeFirst();
        }
        K key = _keys.get(++_position);
        _current = _objects.remove(key);
        if (_current == null) {
            return null;
        }
        if (_current.isEmpty()) {
            _current = null;
            return null;
        }
        return removeFirst();
    }

    private V removeFirst() {
        V object = _current.remove(0);
        if (_current.isEmpty()) {
            _current = null;
        }
        return object;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
