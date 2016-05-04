package pro.documentum.aspects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import pro.documentum.util.IDfSessionInvoker;
import pro.documentum.util.sessions.Sessions;


/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract aspect TransactionalAspect {

    Object around ()throws DfException: execution(* *(..)) && @annotation(Transactional) {
        IDfSession session = findSession(thisJoinPoint);
        if (session == null) {
            return proceed();
        }
        return Sessions.executeInTransaction(session, new IDfSessionInvoker<Object>() {
            @Override
            public Object invoke(IDfSession session) throws DfException {
                return proceed();
            }
        });
    }

    private IDfSession findSession(JoinPoint joinPoint) {
        Object[] arguments = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        IDfSession session = null;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            boolean hasAnnotation = false;
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == SessionHolder.class) {
                    hasAnnotation = true;
                    break;
                }
            }
            if (!hasAnnotation) {
                continue;
            }
            session = getSession(arguments[i]);
            if (session != null) {
                return session;
            }
        }

        session = getSession(target);

        if (session != null) {
            return session;
        }

        for (Object argument : arguments) {
            session = getSession(argument);
            if (session != null) {
                return session;
            }
        }

        return null;
    }

    protected IDfSession getSession(Object object) {
        if (object instanceof IDfSession) {
            return (IDfSession) object;
        }
        if (object instanceof IDfPersistentObject) {
            return ((IDfPersistentObject) object).getObjectSession();
        }
        return null;
    }


}
