package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.logging.Logger;

import kilim.Mailbox;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.AlgorithmNotFoundException;
import nl.minjus.nfi.dt.jhashtools.hashers.DigestAlgorithm;
import nl.minjus.nfi.dt.jhashtools.hashers.FileHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.FileHasherCreator;

/**
 * The actor responsible for hashing files.
 *
 * @author Erwin van Eijk
 *
 */
public class HasherActor extends Actor
{
    private static final Logger LOG = Logger.getLogger(HasherActor.class.getName());

    private final FileHasher fileHasher;

    public HasherActor(final Collection<DigestAlgorithm> algorithms,
        final int numThreads, final Mailbox<Message> inbox, final Mailbox<Message> outbox)
        throws AlgorithmNotFoundException
    {
        super(numThreads, inbox, outbox);

        try {
            fileHasher = FileHasherCreator.createSimple(algorithms);
        } catch (final NoSuchAlgorithmException e) {
            throw new AlgorithmNotFoundException("Could not find message");
        }
    }

    @Override
    public Message act(final Message request) {
        final FileMessage message = (FileMessage) request;
        try {
            final DigestResult digest = fileHasher.getDigest(message.getFile());
            return new DigestMessage(message.getFile(), digest);
        } catch (final IOException ex) {
            return null;
        }
    }
}
