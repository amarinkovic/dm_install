package pro.documentum.dfs.remote.pool;

import com.emc.documentum.fs.rt.context.IServiceContext;

import pro.documentum.util.java.decorators.IDecorator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IServiceContextDecorator extends IDecorator<IServiceContext> {

    void setWrapped(IServiceContext wrapped);

    IServiceContext clone();

}
