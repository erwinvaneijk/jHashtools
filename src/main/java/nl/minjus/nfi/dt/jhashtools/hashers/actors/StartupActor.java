package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;
import java.util.logging.Level;

import kilim.Mailbox;
import kilim.Pausable;

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

    public StartupActor(final File file, final int numThreads, final Mailbox<Message> inbox,
        final Mailbox<Message> outbox)
    {
        super(numThreads, inbox, outbox);
        if (!file.exists()) {
        	throw new IllegalArgumentException("File " + file.getName() + " does not exist.");
        }
        this.file = file;
    }

    @Override
    public void execute() throws Pausable {
        final FileNameGenerator generator = new FileNameGenerator(this.file);
        for (final File message : generator) {
            final FileMessage msg = new FileMessage(message);
            getOutbox().put(msg);
        }
        getOutbox().put(new StopMessage());
    }

    @Override
    public Message act(final Message message) {
        ActingDirectoryHasher.LOG.log(Level.SEVERE, "We should not get here");
        return message;
    }
}