package pro.documentum.persistence.common.query.result;

import com.documentum.fc.client.IDfTypedObject;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IResultFactory<E> {

    E getObject(IDfTypedObject object);

}
