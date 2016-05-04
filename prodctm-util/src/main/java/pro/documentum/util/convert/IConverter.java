package pro.documentum.util.convert;

import java.text.ParseException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IConverter<F, T> {

    T convert(final F obj) throws ParseException;

}
