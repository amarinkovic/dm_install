package pro.documentum.persistence.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.AbstractPersistenceHandler;
import org.datanucleus.store.StoreData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNValues;
import pro.documentum.persistence.common.util.DfExceptions;
import pro.documentum.persistence.common.util.Nucleus;
import pro.documentum.util.queries.bulk.BulkCompositeKeyIterator;
import pro.documentum.util.queries.keys.CompositeKey;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistenceHandlerImpl extends AbstractPersistenceHandler
        implements IPersistenceHandler {

    public PersistenceHandlerImpl(final StoreManager storeMgr) {
        super(storeMgr);
    }

    @Override
    public void close() {

    }

    @Override
    @SuppressWarnings("rawtypes")
    public void insertObject(final ObjectProvider op) {
        assertReadOnlyForUpdateOfObject(op);
        AbstractClassMetaData cmd = op.getClassMetaData();
        ExecutionContext ec = op.getExecutionContext();
        Object objectId = op.getExternalObjectId();
        ManagedConnection mconn = storeMgr.getConnection(ec);
        try {
            StoreData storeData = DNMetaData.getStoreData(ec, cmd);
            Table table = storeData.getTable();
            IDfSession session = (IDfSession) mconn.getConnection();
            IDfPersistentObject dbObject = Nucleus.newObject(session, objectId,
                    cmd, storeMgr);
            DNValues.setNonRelationFields(dbObject, op, table, true);
            Nucleus.save(dbObject);
            DNValues.loadNonRelationalFields(op, dbObject);
        } finally {
            mconn.release();
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void updateObject(final ObjectProvider op, final int[] ints) {
        assertReadOnlyForUpdateOfObject(op);
        AbstractClassMetaData cmd = op.getClassMetaData();
        ExecutionContext ec = op.getExecutionContext();
        Object objectId = op.getExternalObjectId();
        ManagedConnection mconn = storeMgr.getConnection(ec);
        try {
            Table table = DNMetaData.getTable(storeMgr, cmd.getFullClassName());
            IDfSession session = (IDfSession) mconn.getConnection();
            IDfPersistentObject dbObject = Nucleus.getObject(session, objectId);
            DNValues.setFields(dbObject, op, ints, table, false);
            Nucleus.save(dbObject);
            DNValues.loadNonRelationalFields(op, dbObject);
        } finally {
            mconn.release();
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void deleteObject(final ObjectProvider objectProvider) {

    }

    @Override
    @SuppressWarnings("rawtypes")
    public void fetchObject(final ObjectProvider op, final int[] ints) {
        ExecutionContext ec = op.getExecutionContext();
        ManagedConnection mconn = storeMgr.getConnection(ec);
        try {
            Object id = op.getExternalObjectId();
            IDfSession session = (IDfSession) mconn.getConnection();
            IDfPersistentObject dbObject = Nucleus.getObject(session, id);
            DNValues.loadNonRelationalFields(op, dbObject, ints);
        } finally {
            mconn.release();
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void locateObject(final ObjectProvider objectProvider) {

    }

    @Override
    public Object findObject(final ExecutionContext ec, final Object id) {
        // if we are here that means that the object is not in cache
        // or application ignores cache, the problem is to understand
        // whether we are populating object using bulk query or not
        // the main idea is: if target class is known JDO
        // will use fetchObject instead of findObject
        if (id == null) {
            throw new NucleusObjectNotFoundException("Invalid objectId");
        }

        if (Nucleus.hasTargetClass(id)) {
            return null;
        }

        String objectId = Nucleus.getDocumentumId(id);

        ManagedConnection mconn = storeMgr.getConnection(ec);
        try {
            IDfSession session = (IDfSession) mconn.getConnection();
            IDfPersistentObject object = Nucleus.getObject(session, objectId);
            return makeObject(ec, null, object);
        } finally {
            mconn.release();
        }
    }

    @Override
    public <T extends CompositeKey> List<Object> selectObjects(
            final ExecutionContext ec, final AbstractClassMetaData cmd,
            final List<T> keys) {
        ManagedConnection mconn = storeMgr.getConnection(ec);
        try {
            IDfSession session = (IDfSession) mconn.getConnection();
            Table table = DNMetaData.getTable(ec, cmd.getFullClassName());
            Iterator<IDfPersistentObject> objects = BulkCompositeKeyIterator
                    .select(session, table.getName(), keys);
            return makeObjects(ec, cmd, objects);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        } finally {
            mconn.release();
        }
    }

    private List<Object> makeObjects(final ExecutionContext ec,
            final AbstractClassMetaData cmd,
            final Iterator<IDfPersistentObject> objects) {
        List<Object> result = new ArrayList<>();
        while (objects.hasNext()) {
            result.add(makeObject(ec, cmd, objects.next()));
        }
        return result;
    }

    private Object makeObject(final ExecutionContext context,
            final AbstractClassMetaData cmd, final IDfPersistentObject object) {
        Object identity = Nucleus.getIdentity(context,
                DNMetaData.getActual(object, context, cmd), object);
        return context.findObject(identity, true);
    }

}
