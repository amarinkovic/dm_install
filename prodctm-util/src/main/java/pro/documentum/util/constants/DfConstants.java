package pro.documentum.util.constants;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DfConstants {

    private DfConstants() {
        super();
    }

    public static final class Types {

        private static final Set<String> NONSUBTYPABLE;

        static {
            NONSUBTYPABLE = new HashSet<String>();
        }

        private Types() {
            super();
        }

    }

}
