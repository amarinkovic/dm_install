package pro.documentum.dfs.remote.pool;

import java.io.Closeable;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IPooledService extends Closeable {

    void returnToPool();

}
