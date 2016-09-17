package pro.documentum.configservice;

import java.util.HashMap;
import java.util.Map;

import com.documentum.services.config.IContext;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class BaseContext implements IContext {

    private final Map<String, String> _context;

    public BaseContext() {
        _context = new HashMap<>();
    }

    public BaseContext add(final String key, final String value) {
        _context.put(key, value);
        return this;
    }

    @Override
    public String get(final String key) {
        return _context.get(key);
    }

    @Override
    public String toString() {
        return _context.toString();
    }

}
