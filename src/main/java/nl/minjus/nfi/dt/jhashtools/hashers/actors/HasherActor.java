package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.AlgorithmNotFoundException;
import nl.minjus.nfi.dt.jhashtools.hashers.DigestAlgorithm;
import nl.minjus.nfi.dt.jhashtools.hashers.FileHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.FileHasherCreator;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The actor responsible for hashing files.
 *
 * @author Erwin van Eijk
 *
 */
public class HasherActor extends Actor<File, DigestMessage>
{
    private static final Logger LOG = LoggerFactory.getLogger(HasherActor.class.getName());

    private final FileHasher fileHasher;

    public HasherActor(final Collection<DigestAlgorithm> algorithms, final Channel<File> inbox,
        final Channel<DigestMessage> outbox, final Channel<Void> stopChannel,
        final Channel<Void> nextStopChannel, final Fiber fiber) throws AlgorithmNotFoundException
    {
        super(inbox, outbox, stopChannel, nextStopChannel, fiber);

        try {
            fileHasher = FileHasherCreator.createSimple(algorithms);
        } catch (final NoSuchAlgorithmException e) {
            throw new AlgorithmNotFoundException("Could not find message");
        }
    }

    @Override
    public DigestMessage act(final File file) {
        try {
            LOG.debug("hashing: " + file);
            final DigestResult digest = fileHasher.getDigest(file);
            return new DigestMessage(file, digest);
        } catch (final IOException ex) {
            return null;
        }
    }
}
