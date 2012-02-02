package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;

/**
 * Generate <code>File</code> objects to use for further processing.
 *
 * @author Erwin van Eijk
 */
public class FileNameGenerator extends Actor
{
    public FileNameGenerator(final Channel<Message> inbox, final Channel<Message> outbox, Fiber fiber)
    {
        super(inbox, outbox, fiber);
    }

    @Override
    public Message act(final Message message) {
    	FileMessage fileMessage = (FileMessage) message;
    	File startPath = fileMessage.getFile();
        walkTheFile(startPath);
        return new StopMessage();
    }

    private void walkTheFile(final File aPath) {
        if (!aPath.exists()) {
            return;
        }
        if (aPath.isFile()) {
            yield(new FileMessage(aPath));
        } else if (aPath.isDirectory()) {
            for (final File child : aPath.listFiles()) {
                if (child.isFile()) {
                    yield(new FileMessage(child));
                } else {
                    walkTheFile(child);
                }
            }
        }
    }
}
