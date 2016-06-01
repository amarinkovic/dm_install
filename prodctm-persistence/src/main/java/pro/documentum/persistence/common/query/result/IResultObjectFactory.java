package pro.documentum.persistence.common.query.result;

import org.datanucleus.ExecutionContext;

import com.documentum.fc.client.IDfTypedObject;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IResultObjectFactory<E> {

    E getObject(ExecutionContext ec, IDfTypedObject object);

}
