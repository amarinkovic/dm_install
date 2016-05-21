package pro.documentum.util.convert.datastore;

import pro.documentum.util.convert.IConverter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IDataStoreConverter<F, T> extends IConverter<F, T> {

    int getDataStoreType();

}
