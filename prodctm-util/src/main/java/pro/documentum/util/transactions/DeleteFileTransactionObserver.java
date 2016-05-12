package pro.documentum.util.transactions;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.impl.session.ISession;
import com.documentum.fc.client.transaction.IDfTransactionObserver;
import com.documentum.fc.common.DfException;

import pro.documentum.util.logger.Logger;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DeleteFileTransactionObserver extends
        AbstractTransactionObserver {

    private final File _file;

    private DeleteFileTransactionObserver(final File file) {
        _file = file;
    }

    public static IDfTransactionObserver getInstance(final IDfSession session,
            final File file) {
        Objects.requireNonNull(file, "File is null");
        IDfTransactionObserver observer = new DeleteFileTransactionObserver(
                file);
        // check the session is not null
        if (session instanceof ISession) {
            ((ISession) session).getTransaction().getObservable().addObserver(
                    observer);
        }
        return observer;
    }

    @Override
    public void onPostCommit(final IDfSession session, final int xid)
        throws DfException {
        cleanup();
    }

    @Override
    public void onPostRollback(final IDfSession session, final int xid)
        throws DfException {
        cleanup();
    }

    private void cleanup() {
        try {
            Files
                    .walkFileTree(Paths.get(_file.toURI()),
                            new DirectoryCleaner());
        } catch (IOException ex) {
            Logger.error(ex);
        }
    }

    class DirectoryCleaner implements FileVisitor<Path> {

        DirectoryCleaner() {
            super();
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir,
                final BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file,
                final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            Logger.debug("Deleting file {0}", file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file,
                final IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir,
                final IOException exc) throws IOException {
            Logger.debug("Deleting directory {0}", dir);
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }

    }

}
