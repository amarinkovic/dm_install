package pro.documentum.util.transactions;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.documentum.fc.client.transaction.IDfTransactionObserver;

import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DeleteFileTransactionObserverTest {

    @Test
    public void testDelete() throws Exception {
        Path root = Files.createTempDirectory("temp");
        assertTrue(root.toFile().exists());
        logCreated(root);
        for (int i = 0; i < 10; i++) {
            Path directory = Files.createTempDirectory(root, "temp");
            assertTrue(directory.toFile().exists());
            logCreated(directory);
            for (int k = 0; k < 10; k++) {
                Path file = Files.createTempFile(directory, "temp", null);
                assertTrue(directory.toFile().exists());
                logCreated(file);
            }
        }
        IDfTransactionObserver observer = DeleteFileTransactionObserver
                .getInstance(null, root.toFile());
        observer.onPostCommit(null, 0);
        assertFalse(root.toFile().exists());
        observer.onPostCommit(null, 0);
    }

    private void logCreated(Path path) {
        Logger.debug("Created: {0}", path);
    }

}
