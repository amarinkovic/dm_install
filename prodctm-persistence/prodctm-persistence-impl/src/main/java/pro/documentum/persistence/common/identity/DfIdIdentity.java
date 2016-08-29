package pro.documentum.persistence.common.identity;

import java.util.Objects;

import org.datanucleus.identity.DatastoreId;

import com.documentum.fc.common.DfId;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DfIdIdentity implements DatastoreId, Comparable<DfIdIdentity> {

    public static final String STRING_DELIMITER = "[OID]";

    private final String _dfId;

    private final String _className;

    public DfIdIdentity() {
        this(null, DfId.DF_NULLID_STR);
    }

    public DfIdIdentity(final String dfId) {
        this(null, dfId);
    }

    public DfIdIdentity(final String className, final Object id) {
        if (id != null) {
            String strId = (String) id;
            int idx = strId.indexOf(STRING_DELIMITER);
            if (idx > 0) {
                _dfId = strId.substring(0, idx);
                _className = strId.substring(idx + STRING_DELIMITER.length());
            } else {
                _dfId = (String) id;
                _className = className;
            }
        } else {
            _dfId = DfId.DF_NULLID_STR;
            _className = className;
        }
    }

    @Override
    public Object getKeyAsObject() {
        return _dfId;
    }

    public String getId() {
        return _dfId;
    }

    @Override
    public String getTargetClassName() {
        return _className;
    }

    @Override
    public int compareTo(final DfIdIdentity o) {
        return _dfId.compareTo(o._dfId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DfIdIdentity that = (DfIdIdentity) o;
        return Objects.equals(_dfId, that._dfId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_dfId);
    }

    public String toString() {
        return _dfId;
    }

}
