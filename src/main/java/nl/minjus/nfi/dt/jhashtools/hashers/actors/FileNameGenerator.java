package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;
import java.util.logging.Logger;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;

/**
 * Generate <code>File</code> objects to use for further processing.
 *
 * @author Erwin van Eijk
 */
public class FileNameGenerator extends Actor<File, File>
{
    private static final Logger LOG = Logger.getLogger(FileNameGenerator.class.getName());

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
        LOG.fine("Starting on " + startPath);
        walkTheFile(startPath);
        return null;
    }

    private void walkTheFile(final File aPath) {
        if (aPath == null || !aPath.exists()) {
            return;
        }
        if (aPath.isFile()) {
            yield(aPath);
        } else if (aPath.isDirectory()) {
            for (final File child : aPath.listFiles()) {
                if (child.isFile()) {
                    yield(child);
                } else {
                    walkTheFile(child);
                }
            }
        }
    }

    @Override
    protected void yield(final File theFile) {
        LOG.fine("Yielding file " + theFile);
        super.yield(theFile);
    }
}
