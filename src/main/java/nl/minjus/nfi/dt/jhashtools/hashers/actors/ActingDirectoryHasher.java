package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import kilim.Mailbox;
import kilim.Pausable;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.hashers.AbstractDirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;

/**
 * This class is the point of entry for an Actor based concurrent variant for getting a part of a file system processed
 * with digests.
 *
 * @author Erwin van Eijk <erwin.vaneijk@gmail.com>
 */
public class ActingDirectoryHasher extends AbstractDirectoryHasher implements DirectoryHasher
{
    private static final Logger LOG = Logger.getLogger(ActingDirectoryHasher.class.getName());

    private static final int HASHER_ACTOR_POOL_SIZE = 8;
    private static final int REDUCER_ACTOR_POOL_SIZE = 1;

    @Override
    public DirHasherResult getDigests(final File startPath) {
        final DirHasherResult result = new DirHasherResult();
        this.updateDigests(result, startPath);
        return result;
    }

    @Override
    public void updateDigests(final DirHasherResult digests, final File file) {

        try {
            final Mailbox<Message> fileMailbox = new Mailbox<Message>();
            final Mailbox<Message> reducerMailbox = new Mailbox<Message>();

            final HasherActor hasherActor =
                new HasherActor(this.getTheAlgorithms(), HASHER_ACTOR_POOL_SIZE, fileMailbox, reducerMailbox);
            final ReducerActor reducerActor =
                new ReducerActor(digests, REDUCER_ACTOR_POOL_SIZE, reducerMailbox);
            hasherActor.start();
            reducerActor.start();
            final FileNameGenerator generator = new FileNameGenerator(file);
            for (final File message : generator) {
                final FileMessage msg = new FileMessage(message);
                fileMailbox.put(msg);
            }
            fileMailbox.put(new StopMessage());
        } catch (final Pausable p) {
            LOG.log(Level.INFO, "Not really a problem, I guess", p);
        }
    }

}
