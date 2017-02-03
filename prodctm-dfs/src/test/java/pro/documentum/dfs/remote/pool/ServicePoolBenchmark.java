package pro.documentum.dfs.remote.pool;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Ignore;
import org.junit.Test;

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
            for (int i = 0; i < 1; i++) {
                threads.add(new Thread(new Task(new PoolBenchmark(), counter)));
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
                assertTrue(((curValue - prevValue) * 1000 / SLEEP_TIME) > 1000);
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

        private final IServiceContext context = ContextFactory.getInstance()
                .newContext();

        public DFSBenchmark() {
            super();
        }

        @Override
        public void op() throws Exception {
            IObjectService service = ServiceFactory.getInstance()
                    .getRemoteService(IObjectService.class, context);
            ((IRemoteService) service).getBindingProvider();
        }

    }

    static class PoolBenchmark implements IBenchmark {

        private static final IServicePool POOL = ServicePool.getInstance();

        private final IServiceContext context = ContextFactory.getInstance()
                .newContext();

        public PoolBenchmark() {
            super();
        }

        @Override
        public void op() throws Exception {
            IObjectService service = POOL.getService(IObjectService.class,
                    context);
            ((IRemoteService) service).getBindingProvider();
            ((IPooledService) service).returnToPool();
        }

    }

    public interface IBenchmark {

        void op() throws Exception;

    }

}
