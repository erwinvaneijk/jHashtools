package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import kilim.ExitMsg;
import kilim.Mailbox;
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
    static final Logger LOG = Logger.getLogger(ActingDirectoryHasher.class.getName());

    private static final int HASHER_ACTOR_POOL_SIZE = 8;
    private static final int REDUCER_ACTOR_POOL_SIZE = 1;

    final Mailbox<Message> fileMailbox;
    private final Mailbox<Message> reducerMailbox;
    private final Mailbox<ExitMsg> exitMailbox;

    /**
     * Constructor.
     */
    public ActingDirectoryHasher()
    {
        fileMailbox = new Mailbox<Message>();
        reducerMailbox = new Mailbox<Message>();
        exitMailbox = new Mailbox<ExitMsg>();
    }

    /**
     * Constructor.
     *
     * @param algorithm
     *          The algorithm to initialize with.
     * @throws NoSuchAlgorithmException
     *          when the algorithm is not supported.
     */
    public ActingDirectoryHasher(final String algorithm) throws NoSuchAlgorithmException
    {
        super(algorithm);
        fileMailbox = new Mailbox<Message>();
        reducerMailbox = new Mailbox<Message>();
        exitMailbox = new Mailbox<ExitMsg>();
    }

    @Override
    public DirHasherResult getDigests(final File startPath) {
        final DirHasherResult result = new DirHasherResult();
        this.updateDigests(result, startPath);
        return result;
    }

    @Override
    public void updateDigests(final DirHasherResult digests, final File file) {

        final StartupActor startupActor = new StartupActor(file, 1, null, fileMailbox);
        final HasherActor hasherActor = new HasherActor(this.getTheAlgorithms(), HASHER_ACTOR_POOL_SIZE,
            fileMailbox, reducerMailbox);
        final ReducerActor reducerActor = new ReducerActor(digests, REDUCER_ACTOR_POOL_SIZE, reducerMailbox);
        startupActor.start();
        startupActor.informOnExit(exitMailbox);
        hasherActor.start();
        hasherActor.informOnExit(exitMailbox);
        reducerActor.start();
        reducerActor.informOnExit(exitMailbox);

        exitMailbox.getb();
    }
}
