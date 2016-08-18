package pro.documentum.persistence.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.AbstractPersistenceHandler;
import org.datanucleus.store.StoreData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.DfObjectNotFoundException;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

import pro.documentum.persistence.common.fieldmanager.FetchFieldManager;
import pro.documentum.persistence.common.fieldmanager.StoreFieldManager;
import pro.documentum.persistence.common.util.DNFields;
import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNVersions;
import pro.documentum.persistence.common.util.DfExceptions;
import pro.documentum.persistence.common.util.Nucleus;
import pro.documentum.util.ids.DfIdUtil;
import pro.documentum.util.logger.Logger;
import pro.documentum.util.objects.DfObjects;
import pro.documentum.util.objects.changes.ChangesProcessor;
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
            IDfPersistentObject dbObject = newObject(session, objectId, cmd);
            setNonRelationFields(dbObject, op, table, true);
            save(dbObject);
            loadNonRelationalFields(op, dbObject);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
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
            IDfPersistentObject dbObject = getObject(session, objectId);
            setFields(dbObject, op, ints, table, false);
            save(dbObject);
            loadNonRelationalFields(op, dbObject);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
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
            IDfPersistentObject dbObject = getObject(session, id);
            loadNonRelationalFields(op, dbObject, ints);
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

        String objectId = getDocumentumId(id);

        ManagedConnection mconn = storeMgr.getConnection(ec);
        try {
            IDfSession session = (IDfSession) mconn.getConnection();
            IDfPersistentObject object = getObject(session, objectId);
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

    private void loadNonRelationalFields(final ObjectProvider<?> op,
            final IDfPersistentObject object, final int[] fields) {
        AbstractClassMetaData cmd = op.getClassMetaData();
        int[] persistent = DNFields.getPersistentFields(cmd, fields);
        int[] nonPersistent = DNFields.getNonPersistentFields(cmd, fields);
        for (int fieldNumber : nonPersistent) {
            op.replaceField(fieldNumber, op.provideField(fieldNumber));
        }
        StoreManager storeMgr = op.getStoreManager();
        Table table = DNMetaData.getTable(storeMgr, cmd.getFullClassName());
        FetchFieldManager fieldManager = new FetchFieldManager(op, object,
                table);
        op.replaceFields(persistent, fieldManager);
        DNVersions.processVersion(op);
    }

    private void loadNonRelationalFields(final ObjectProvider<?> op,
            final IDfPersistentObject object) {
        AbstractClassMetaData cmd = op.getClassMetaData();
        ExecutionContext ec = op.getExecutionContext();
        int[] nonRelPositions = cmd.getNonRelationMemberPositions(
                ec.getClassLoaderResolver(), ec.getMetaDataManager());
        loadNonRelationalFields(op, object, nonRelPositions);
    }

    private void setNonRelationFields(final IDfPersistentObject object,
            final ObjectProvider<?> op, final Table table, final boolean insert) {
        ExecutionContext ec = op.getExecutionContext();
        AbstractClassMetaData cmd = op.getClassMetaData();
        int[] nonRelPositions = cmd.getNonRelationMemberPositions(
                ec.getClassLoaderResolver(), ec.getMetaDataManager());
        if (!insert) {
            nonRelPositions = DNFields.getDirtyFields(op, nonRelPositions);
        }
        try {
            setFields(object, op, nonRelPositions, table, insert);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

    private void setFields(final IDfPersistentObject object,
            final ObjectProvider<?> op, final int[] fields, final Table table,
            final boolean insert) throws DfException {
        StoreFieldManager fm = new StoreFieldManager(op, object, insert, table);
        op.provideFields(fields, fm);
        ChangesProcessor.process(object, fm.getValues());
    }

    private void save(final IDfPersistentObject object) throws DfException {
        Logger.debug("Trying to save object {0}", object.getObjectId());
        if (!object.isDirty()) {
            Logger.debug("Object {0} is not dirty, skipping",
                    object.getObjectId());
            return;
        }
        if (object instanceof IDfSysObject) {
            IDfSysObject sysObject = (IDfSysObject) object;
            Logger.debug("Object {0} is sysobject, saving lock, "
                    + "current lock owner is: {1}", object.getObjectId(),
                    sysObject.getLockOwner());
            sysObject.saveLock();
            return;
        }
        object.save();
    }

    private IDfPersistentObject newObject(final IDfSession session,
            final Object id, final AbstractClassMetaData cmd)
        throws DfException {
        Table table = DNMetaData.getTable(storeMgr, cmd.getFullClassName());
        String objectId = getDocumentumId(id);
        return DfObjects.newObject(session, table.getName(), objectId);
    }

    public String getDocumentumId(final Object id) {
        String objectId = null;
        if (IdentityUtils.isDatastoreIdentity(id)) {
            objectId = (String) IdentityUtils
                    .getTargetKeyForDatastoreIdentity(id);
        } else if (IdentityUtils.isSingleFieldIdentity(id)) {
            objectId = (String) IdentityUtils
                    .getTargetKeyForSingleFieldIdentity(id);
        }
        if (DfIdUtil.isNotObjectId(objectId)) {
            throw new NucleusObjectNotFoundException("Invalid objectId: "
                    + objectId);
        }
        return objectId;
    }

    public IDfPersistentObject getObject(final IDfSession session,
            final Object id) {
        return getObject(session, getDocumentumId(id));
    }

    public IDfPersistentObject getObject(final IDfSession session,
            final String objectId) {
        try {
            Logger.debug("Trying to fetch object with id {0}", objectId);
            return session.getObject(DfIdUtil.getId(objectId));
        } catch (DfObjectNotFoundException ex) {
            throw DfExceptions.notFoundException(ex);
        } catch (DfException ex) {
            throw DfExceptions.dataStoreException(ex);
        }
    }

}
