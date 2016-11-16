package pro.documentum.aspects;

import com.documentum.fc.common.DfException;

import pro.documentum.util.exceptions.DfExceptions;
import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public aspect SafeFetchAspect {

    pointcut getObject(): call (* com.documentum.fc.client.IDfSession.getObject(..));
    pointcut getObjectByQualification(): call (* com.documentum.fc.client.IDfSession.getObjectByQualification(..));
    pointcut getObjectWithCaching():  call (* com.documentum.fc.client.IDfSession.getObjectWithCaching(..));
    pointcut fetch(): (getObject() || getObjectByQualification() || getObjectWithCaching()) && !within(SafeFetchAspect);

    Object around ()throws DfException: fetch() {
        DfException ex = null;
        for (int i = 0; i < 10; i++) {
            try {
                return proceed();
            } catch (DfException e) {
                ex = e;
                if (DfExceptions.isFetchSoft(e)) {
                    Logger.debug("Got soft exception on {0} iteration", e, i + 1);
                    continue;
                }
                throw ex;
            }
        }
        throw ex;
    }

}
