package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Set;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class HasherActor extends Actor<File, DigestMessage>
{
    private static final Logger LOG = LoggerFactory.getLogger(HasherActor.class.getName());

    private final FileHasher fileHasher;

    private final Set<File> claimedFiles;

    /**
     * Constructor.
     *
     * @param algorithms
     *            the algorithms to compute.
     * @param inbox
     *            the channel the files to hash arrive on. Every hasher subscribed to it receives every file.
     * @param outbox
     *            the channel to publish the digests on.
     * @param stopChannel
     *            the channel that signals this actor to stop.
     * @param nextStopChannel
     *            the channel to signal when this actor stops.
     * @param fiber
     *            the fiber to run on.
     * @param theClaimedFiles
     *            the files claimed by the hashers sharing <code>inbox</code>, so that each file is hashed only once.
     * @throws AlgorithmNotFoundException
     *             when one of the algorithms is not supported.
     */
    public HasherActor(final Collection<DigestAlgorithm> algorithms, final Channel<File> inbox,
        final Channel<DigestMessage> outbox, final Channel<Void> stopChannel,
        final Channel<Void> nextStopChannel, final Fiber fiber, final Set<File> theClaimedFiles)
        throws AlgorithmNotFoundException
    {
        super(inbox, outbox, stopChannel, nextStopChannel, fiber);
        this.claimedFiles = theClaimedFiles;

        try {
            fileHasher = FileHasherCreator.createSimple(algorithms);
        } catch (final NoSuchAlgorithmException e) {
            throw new AlgorithmNotFoundException("Could not find message");
        }
    }

    @Override
    public DigestMessage act(final File file) {
        // The file name generator publishes null once it has walked its path.
        if (file == null || !claimedFiles.add(file)) {
            return null;
        }
        try {
            LOG.debug("hashing: " + file);
            final DigestResult digest = fileHasher.getDigest(file);
            return new DigestMessage(file, digest);
        } catch (final IOException ex) {
            return null;
        }
    }
}
