package pro.documentum.persistence.common.util;

import org.datanucleus.metadata.RelationType;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNRelation {

    private DNRelation() {
        super();
    }

    public static boolean isNone(final RelationType relationType) {
        return relationType == RelationType.NONE;
    }

}
