package pro.documentum.util;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface ISessionInvoker<T, S, E extends Throwable> {

    T invoke(S session) throws E;

}
