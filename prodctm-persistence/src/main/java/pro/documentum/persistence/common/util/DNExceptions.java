package pro.documentum.persistence.common.util;

import org.datanucleus.exceptions.NucleusException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNExceptions {

    private DNExceptions() {
        super();
    }

    public static NucleusException noPropertySpecified(final String propertyName) {
        return new NucleusException("You haven't specified persistence "
                + "property '" + propertyName + "' (or alias)");
    }

}
