package pro.documentum.dfs.remote.pool;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Ignore;
import org.junit.Test;

import com.emc.documentum.fs.datamodel.core.ObjectIdentity;
import com.emc.documentum.fs.datamodel.core.Qualification;
import com.emc.documentum.fs.datamodel.core.context.Identity;
import com.emc.documentum.fs.datamodel.core.context.RepositoryIdentity;
import com.emc.documentum.fs.rt.context.ContextFactory;
import com.emc.documentum.fs.rt.context.IServiceContext;
import com.emc.documentum.fs.rt.context.ServiceFactory;
import com.emc.documentum.fs.rt.context.impl.IRemoteService;
import com.emc.documentum.fs.services.core.client.IObjectService;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Ignore
public class ServicePoolBenchmark {

    public static final int SLEEP_TIME = 5000;

    @Test
    public void test() throws Exception {
        List<Thread> threads = new ArrayList<>();
        AtomicLong counter = new AtomicLong(0);
        try {
            for (int i = 0; i < 2; i++) {
                threads.add(new Thread(new Task(new DFSBenchmark(), counter)));
            }

            for (Thread thread : threads) {
                thread.start();
            }

            long prevValue = counter.get();
            for (int iteration = 1; iteration < 11; iteration++) {
                Thread.sleep(SLEEP_TIME);
                long curValue = counter.get();
                System.out.println("Ops per second: "
                        + ((curValue - prevValue) * 1000 / SLEEP_TIME)
                        + ", iteration: " + iteration);
                if (iteration > 1) {
                    assertTrue(((curValue - prevValue) * 1000 / SLEEP_TIME) > 500);
                }
                prevValue = curValue;
            }
        } finally {
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }
    }

    static class Task implements Runnable {

        private final IBenchmark _benchmark;

        private final AtomicLong _counter;

        public Task(IBenchmark benchmark, AtomicLong counter) {
            _benchmark = benchmark;
            _counter = counter;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    _benchmark.op();
                    _counter.incrementAndGet();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }

    static class DFSBenchmark implements IBenchmark {

        private final IServiceContext context;

        private final ObjectIdentity<Qualification<String>> objectIdentity;

        public DFSBenchmark() {
            context = ContextFactory.getInstance().newContext();
            Identity identity = new RepositoryIdentity("DCTM_DEV", "dmadmin",
                    "dmadmin", null);
            context.setIdentities(Arrays.asList(identity));
            objectIdentity = new ObjectIdentity<>(new Qualification<>(
                    "dm_server_config"), "DCTM_DEV");

        }

        @Override
        public void op() throws Exception {
            IObjectService service = ServiceFactory.getInstance()
                    .getRemoteService(IObjectService.class, context);
            ((IRemoteService) service).getBindingProvider();
            // DataPackage dataPackage = service.get(new ObjectIdentitySet(
            // objectIdentity), null);
        }

    }

    public interface IBenchmark {

        void op() throws Exception;

    }

}
