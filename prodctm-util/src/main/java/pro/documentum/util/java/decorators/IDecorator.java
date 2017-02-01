package pro.documentum.util.java.decorators;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IDecorator<T> {

    T unwrap();

    void setProxy(T proxy);

}
