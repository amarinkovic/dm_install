package pro.documentum.util.exceptions;

import org.junit.Ignore;
import org.junit.Test;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.commands.admin.DfAdminCommand;
import com.documentum.fc.commands.admin.IDfAdminCommand;
import com.documentum.fc.commands.admin.impl.ApplyExecSQL;
import com.documentum.fc.common.DfException;

import pro.documentum.junit.DfcTestSupport;
import pro.documentum.util.ids.DfIdUtil;
import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DfExceptionsTest extends DfcTestSupport {

    @Test
    @Ignore
    public void testSoftFetch() throws Exception {
        final IDfSession s1 = Sessions.brandNew(getSession()
                .getSessionManager(), getSession().getDocbaseName());
        final IDfSession s2 = Sessions.brandNew(getSession()
                .getSessionManager(), getSession().getDocbaseName());

        TestFetch testFetch = new TestFetch(s2, "09024be98001f94e");
        Thread t = new Thread(testFetch);
        t.start();

        ApplyExecSQL cmd = (ApplyExecSQL) DfAdminCommand
                .getCommand(IDfAdminCommand.APPLY_EXEC_SQL);
        cmd.setQuery("update dm_sysobject_s set i_vstamp=i_vstamp+1 "
                + "where r_object_id='09024be98001f94e'");

        for (int i = 0; i < 10000; i++) {
            cmd.execute(s1);
        }

        t.interrupt();
        assertFalse(testFetch._failed);
    }

    class TestFetch implements Runnable {

        private final IDfSession _session;

        private final String _objectId;

        private boolean _failed;

        public TestFetch(IDfSession session, String objectId) {
            _session = session;
            _objectId = objectId;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 100000; i++) {
                    _session.getObject(DfIdUtil.getId(_objectId));
                    _session.flushObject(DfIdUtil.getId(_objectId));
                }
            } catch (DfException ex) {
                _failed = true;
            }
        }
    }

}
