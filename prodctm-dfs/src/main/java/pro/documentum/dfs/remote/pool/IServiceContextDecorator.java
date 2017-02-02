package pro.documentum.dfs.remote.pool;

import com.emc.documentum.fs.rt.context.IServiceContext;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IServiceContextDecorator {

    IServiceContext unwrap();

    void setWrapped(IServiceContext wrapped);

    IServiceContext clone();

}
