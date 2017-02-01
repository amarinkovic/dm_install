package pro.documentum.dfs.remote.pool;

import com.emc.documentum.fs.rt.context.IContextHolder;
import com.emc.documentum.fs.rt.context.IServiceContext;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IServicePool {

    <T extends IContextHolder> T getService(Class<T> serviceClass,
            IServiceContext context) throws Exception;

    <T extends IContextHolder> T getService(Class<T> serviceClass,
            IServiceContext context, String moduleName, String contextRoot)
        throws Exception;

}
