package pro.documentum.persistence.common.util;

import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;

import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DfExceptions {

    private DfExceptions() {
        super();
    }

    public static NucleusDataStoreException dataStoreException(
            final DfException ex) {
        return new NucleusDataStoreException(ex.getMessage(), ex);
    }

    public static NucleusObjectNotFoundException notFoundException(
            final DfException ex) {
        return new NucleusObjectNotFoundException(ex.getMessage(), ex);
    }

    public static NucleusException nucleusException(final DfException ex) {
        return new NucleusException(ex.getMessage(), ex);
    }

}
