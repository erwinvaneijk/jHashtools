package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generate <code>File</code> objects to use for further processing.
 *
 * @author Erwin van Eijk
 */
public class FileNameGenerator extends Actor<File, File>
{
    private static final Logger LOG = LoggerFactory.getLogger(FileNameGenerator.class);

    public FileNameGenerator(final Channel<File> inbox,
        final Channel<File> outbox,
        final Channel<Void> stopChannel,
        final Channel<Void> nextStopChannel,
        final Fiber fiber)
    {
        super(inbox, outbox, stopChannel, nextStopChannel, fiber);
    }

    @Override
    public File act(final File startPath) {
        LOG.debug("Starting on {}", startPath);
        walkTheFile(startPath);
        return null;
    }

    private void walkTheFile(final File aPath) {
        if (aPath == null || !aPath.exists()) {
            return;
        }
        if (aPath.isFile()) {
            emit(aPath);
        } else if (aPath.isDirectory()) {
            for (final File child : aPath.listFiles()) {
                if (child.isFile()) {
                    emit(child);
                } else {
                    walkTheFile(child);
                }
            }
        }
    }

    @Override
    protected void emit(final File theFile) {
        LOG.debug("Yielding file {}", theFile);
        super.emit(theFile);
    }
}
