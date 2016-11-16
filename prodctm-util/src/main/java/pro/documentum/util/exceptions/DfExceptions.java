package pro.documentum.util.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.common.IDfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DfExceptions {

    public static final List<String> FETCH_SOFT;

    static {
        List<String> restartable = new ArrayList<>();
        restartable.add("[DM_OBJ_MGR_E_UNABLE_TO_FETCH_"
                + "CONSISTENT_OBJECT_SNAPSHOT]");
        restartable.add("[DM_OBJ_MGR_E_UNABLE_TO_APPLY_"
                + "ASPECT_ATTR_CHANGES]");
        FETCH_SOFT = Collections.unmodifiableList(restartable);
    }

    private DfExceptions() {
        super();
    }

    private static boolean thContains(final Throwable t,
            final List<String> messages) {
        if (t == null) {
            return false;
        }
        if (t instanceof IDfException) {
            if (dfContains((IDfException) t, messages)) {
                return true;
            }
        }
        if (hasMessage(t.getMessage(), messages)) {
            return true;
        }
        return thContains(t.getCause(), messages);
    }

    private static boolean dfContains(final IDfException ex,
            final List<String> messages) {
        if (ex == null) {
            return false;
        }
        if (hasMessage(ex.getMessage(), messages)) {
            return true;
        }
        return dfContains(ex.getNextException(), messages);
    }

    private static boolean hasMessage(final String message,
            final List<String> messages) {
        if (StringUtils.isBlank(message)) {
            return false;
        }
        for (String msg : messages) {
            if (message.contains(msg)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFetchSoft(final Throwable t) {
        return thContains(t, FETCH_SOFT);
    }

}
