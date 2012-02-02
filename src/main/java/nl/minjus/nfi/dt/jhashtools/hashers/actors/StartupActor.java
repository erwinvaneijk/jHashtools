package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;
import java.util.logging.Level;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;

/**
 * This actor is needed to start things up. Therefore it does not implement <code>act</code>, but it deals with
 * execute directly.
 *
 * @author Erwin van Eijk
 *
 */
class StartupActor extends Actor
{
	private final File file;

    public StartupActor(final File file, final int numThreads, final Channel<Message> inbox,
        final Channel<Message> outbox, Fiber fiber)
    {
        super(inbox, outbox, fiber);
        if (!file.exists()) {
        	throw new IllegalArgumentException("File " + file.getName() + " does not exist.");
        }
        this.file = file;
    }

    @Override
    public Message act(final Message message) {
        ActingDirectoryHasher.LOG.log(Level.SEVERE, "We should not get here");
        return message;
    }
}