package pro.documentum.util;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IDfSessionInvoker<T> extends
        ISessionInvoker<T, IDfSession, DfException> {

}
