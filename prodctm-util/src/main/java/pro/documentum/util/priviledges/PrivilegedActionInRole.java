package pro.documentum.util.priviledges;

import java.security.AccessControlContext;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.List;

import com.documentum.fc.client.impl.bof.security.RoleRequestManager;
import com.documentum.fc.client.security.DfRoleSpec;

import pro.documentum.util.ISessionInvoker;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PrivilegedActionInRole<T> implements PrivilegedAction<T> {

    private final PrivilegedAction<T> _action;

    private final DfRoleSpec _roleSpec;

    public PrivilegedActionInRole(final DfRoleSpec roleSpec,
            final PrivilegedAction<T> action) {
        _roleSpec = roleSpec;
        _action = action;
    }

    public static <T, E extends Throwable> T privilegedRequest(
            final ISessionInvoker<T, Void, E> invoker,
            final List<String> groupNames) throws E {
        return privilegedRequest(invoker,
                groupNames.toArray(new String[groupNames.size()]));
    }

    public static <T, E extends Throwable> T privilegedRequest(
            final ISessionInvoker<T, Void, E> invoker,
            final String... groupNames) throws E {
        try {
            for (String groupName : groupNames) {
                startPrivilegedRequest(groupName);
            }
            return invoker.invoke(null);
        } finally {
            for (int i = 0, n = groupNames.length; i < n; i++) {
                stopPrivilegedRequest(groupNames[n - i - 1]);
            }
        }
    }

    public static void stopPrivilegedRequest(final String groupName) {
        stopPrivilegedRequest(new DfRoleSpec(groupName));
    }

    public static DfRoleSpec startPrivilegedRequest(final String groupName) {
        return startPrivilegedRequest(new DfRoleSpec(groupName));
    }

    public static DfRoleSpec startPrivilegedRequest(final String groupName,
            final String docbaseName) {
        return startPrivilegedRequest(new DfRoleSpec(groupName, docbaseName));
    }

    public static void stopPrivilegedRequest(final DfRoleSpec roleSpec) {
        RoleRequestManager requestManager = RoleRequestManager.getInstance();
        requestManager.pop(roleSpec);
    }

    public static DfRoleSpec startPrivilegedRequest(final DfRoleSpec roleSpec) {
        RoleRequestManager requestManager = RoleRequestManager.getInstance();
        requestManager.push(roleSpec, new AccessControlContext(
                new ProtectionDomain[] {}));
        return roleSpec;
    }

    @Override
    public T run() {
        try {
            startPrivilegedRequest(_roleSpec);
            return _action.run();
        } finally {
            stopPrivilegedRequest(_roleSpec);
        }
    }

}
