package pro.documentum.persistence.common;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.identity.IdentityManager;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.AbstractPersistenceHandler;
import org.datanucleus.store.StoreData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.schema.table.Table;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;

import pro.documentum.persistence.common.util.DNMetaData;
import pro.documentum.persistence.common.util.DNValues;
import pro.documentum.persistence.common.util.Nucleus;
import pro.documentum.util.ids.DfIdUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DocumentumPersistenceHandler extends AbstractPersistenceHandler {

    public DocumentumPersistenceHandler(final StoreManager storeMgr) {
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
            Table table = storeMgr.getStoreDataForClass(cmd.getFullClassName())
                    .getTable();
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
            IDfSession session = (IDfSession) mconn.getConnection();
            IDfPersistentObject dbObject = Nucleus.getObject(session,
                    op.getExternalObjectId());
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

        String targetClass = IdentityUtils
                .getTargetClassNameForIdentitySimple(id);
        if (StringUtils.isNotBlank(targetClass)) {
            return null;
        }

        String objectId = String.valueOf(IdentityUtils
                .getTargetKeyForDatastoreIdentity(id));

        if (DfIdUtil.isNotObjectId(objectId)) {
            throw new NucleusObjectNotFoundException("Invalid objectId: "
                    + objectId);
        }

        ManagedConnection mconn = storeMgr.getConnection(ec);
        try {
            IDfSession session = (IDfSession) mconn.getConnection();
            IDfPersistentObject object = Nucleus.getObject(session, objectId);
            String className = DNMetaData.getClassNameForObject(ec, object);
            if (className == null) {
                throw new NucleusException("No class for object: " + objectId);
            }
            IdentityManager im = ec.getNucleusContext().getIdentityManager();
            Object targetId = im.getDatastoreId(className, objectId);
            return ec.findObject(targetId, true);
        } finally {
            mconn.release();
        }
    }

}
