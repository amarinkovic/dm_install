package pro.documentum.aspects;

import java.lang.reflect.Method;
import java.util.Stack;

import org.aspectj.lang.reflect.MethodSignature;

import com.documentum.fc.client.security.DfRoleSpec;

import pro.documentum.util.priviledges.PrivilegedActionInRole;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public aspect PrivilegedActionAspect {

    Object around (): execution(* *(..)) && @annotation(pro.documentum.aspects.DfPrivileged) {
        Method method = MethodSignature.class.cast(thisJoinPoint.getSignature()).getMethod();
        DfPrivileged annotation = method.getAnnotation(DfPrivileged.class);
        if (annotation == null) {
            return proceed();
        }
        String[] roles = annotation.roles();
        if (roles == null || roles.length == 0) {
            roles = new String[]{PrivilegedRoles.DM_SUPERUSERS_DYNAMIC};
        }
        Stack<DfRoleSpec> specs = new Stack<>();
        try {
            for (String role : roles) {
                specs.push(PrivilegedActionInRole.startPrivilegedRequest(role));
            }
            return proceed();
        } finally {
            while (!specs.empty()) {
                PrivilegedActionInRole.stopPrivilegedRequest(specs.pop());
            }
        }
    }

}
