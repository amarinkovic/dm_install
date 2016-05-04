package pro.documentum.util.queries.bulk;

import java.io.Closeable;
import java.util.Iterator;

import com.documentum.fc.client.IDfPersistentObject;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IBulkIterator<O extends IDfPersistentObject> extends
        Iterator<O>, Closeable {

}
