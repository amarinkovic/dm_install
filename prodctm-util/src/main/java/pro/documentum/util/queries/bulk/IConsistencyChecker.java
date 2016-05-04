package pro.documentum.util.queries.bulk;

import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IConsistencyChecker {

    boolean skip(String objectId) throws DfException;

}
