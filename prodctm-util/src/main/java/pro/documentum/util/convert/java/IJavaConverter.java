package pro.documentum.util.convert.java;

import java.util.List;

import pro.documentum.util.convert.IConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IJavaConverter<F, T> extends IConverter<F, T> {

    List<Class<T>> getJavaType();

}
