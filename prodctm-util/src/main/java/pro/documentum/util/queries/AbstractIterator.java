package pro.documentum.util.queries;

import java.io.Closeable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractIterator implements Iterator<IDfTypedObject>,
        Closeable, IDfTypedObject {

    private final IDfCollection _collection;

    public AbstractIterator(final IDfCollection collection) {
        _collection = Objects.requireNonNull(collection);
    }

    @Override
    public final void close() {
        Queries.close(_collection);
    }

    protected final int getState() {
        return _collection.getState();
    }

    protected final boolean doNext() throws DfException {
        return _collection.next();
    }

    protected final IDfTypedObject getTypedObject() throws DfException {
        return _collection.getTypedObject();
    }

    @Override
    public void appendBoolean(final String attrName, final boolean value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendDouble(final String attrName, final double value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendId(final String attrName, final IDfId value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendInt(final String attrName, final int value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendString(final String attrName, final String value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendTime(final String attrName, final IDfTime value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendValue(final String attrName, final IDfValue value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String dump() throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Enumeration enumAttrs() throws DfException {
        return _collection.enumAttrs();
    }

    @Override
    public int findAttrIndex(final String attrName) throws DfException {
        return _collection.findAttrIndex(attrName);
    }

    @Override
    public int findBoolean(final String attrName, final boolean value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int findDouble(final String attrName, final double value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int findId(final String attrName, final IDfId value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int findInt(final String attrName, final int value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int findString(final String attrName, final String value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int findTime(final String attrName, final IDfTime value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int findValue(final String attrName, final IDfValue value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAllRepeatingStrings(final String attrName, final String sep)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfAttr getAttr(final int index) throws DfException {
        return _collection.getAttr(index);
    }

    @Override
    public int getAttrCount() throws DfException {
        return _collection.getAttrCount();
    }

    @Override
    public int getAttrDataType(final String attrName) throws DfException {
        return _collection.getAttrDataType(attrName);
    }

    @Override
    public boolean getBoolean(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getDouble(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfId getId(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getInt(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLong(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfId getObjectId() throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getRepeatingBoolean(final String attrName, final int index)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getRepeatingDouble(final String attrName, final int index)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfId getRepeatingId(final String attrName, final int index)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRepeatingInt(final String attrName, final int index)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getRepeatingLong(final String attrName, final int index)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRepeatingString(final String attrName, final int index)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfTime getRepeatingTime(final String attrName, final int index)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfValue getRepeatingValue(final String attrName, final int index)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfSession getObjectSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfSession getOriginalSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfSession getSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfSessionManager getSessionManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSessionManager(final IDfSessionManager sessionManager)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfTime getTime(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfValue getValue(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public IDfValue getValueAt(final int index) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getValueCount(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasAttr(final String attrName) throws DfException {
        return _collection.hasAttr(attrName);
    }

    @Override
    public void insertBoolean(final String attrName, final int index,
            final boolean value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertDouble(final String attrName, final int index,
            final double value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertId(final String attrName, final int index,
            final IDfId value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertInt(final String attrName, final int index,
            final int value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertString(final String attrName, final int index,
            final String value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertTime(final String attrName, final int index,
            final IDfTime value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertValue(final String attrName, final int index,
            final IDfValue value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAttrRepeating(final String attrName) throws DfException {
        return _collection.isAttrRepeating(attrName);
    }

    @Override
    public boolean isNull(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(final String attrName, final int index)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAll(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBoolean(final String attrName, final boolean value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDouble(final String attrName, final double value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setId(final String attrName, final IDfId value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setInt(final String attrName, final int value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNull(final String attrName) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRepeatingBoolean(final String attrName, final int index,
            final boolean value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRepeatingDouble(final String attrName, final int index,
            final double value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRepeatingId(final String attrName, final int index,
            final IDfId value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRepeatingInt(final String attrName, final int index,
            final int value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRepeatingString(final String attrName, final int index,
            final String value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRepeatingTime(final String attrName, final int index,
            final IDfTime value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRepeatingValue(final String attrName, final int index,
            final IDfValue value) throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setString(final String attrName, final String value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTime(final String attrName, final IDfTime value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setValue(final String attrName, final IDfValue value)
        throws DfException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void truncate(final String attrName, final int index)
        throws DfException {
        throw new UnsupportedOperationException();
    }

}
