package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.hashers.AbstractDirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;

import org.jetlang.core.Disposable;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the point of entry for an Actor based concurrent variant for getting a part of a file system processed
 * with digests.
 *
 * @author Erwin van Eijk <erwin.vaneijk@gmail.com>
 */
public class ActingDirectoryHasher extends AbstractDirectoryHasher implements DirectoryHasher
{
    static final Logger LOG = LoggerFactory.getLogger(ActingDirectoryHasher.class.getName());

    private static final int NUM_REDUCERS = 1;

    private static final int NUM_FILE_FEEDERS = 1;

    private final PoolFiberFactory fiberFactory;

    private final ExecutorService executorService;

    private final Channels channels;

    /**
     * Constructor.
     */
    public ActingDirectoryHasher()
    {
        executorService = Executors.newCachedThreadPool();
        fiberFactory = new PoolFiberFactory(executorService);
        channels = new Channels();
    }

    /**
     * Constructor.
     *
     * @param algorithm
     *            The algorithm to initialize with.
     * @throws NoSuchAlgorithmException
     *             when the algorithm is not supported.
     */
    public ActingDirectoryHasher(final String algorithm) throws NoSuchAlgorithmException
    {
        super(algorithm);
        executorService = Executors.newCachedThreadPool();
        fiberFactory = new PoolFiberFactory(executorService);
        channels = new Channels();
    }

    /**
     * Constructor.
     *
     * @param digests
     */
    public ActingDirectoryHasher(final Collection<String> digests) throws NoSuchAlgorithmException
    {
        super(digests);
        executorService = Executors.newCachedThreadPool();
        fiberFactory = new PoolFiberFactory(executorService);
        channels = new Channels();
    }

    @Override
    public DirHasherResult getDigests(final File startPath) {
        if (!startPath.exists()) {
            throw new IllegalArgumentException("File " + startPath + " does not exist.");
        }
        final DirHasherResult result = new DirHasherResult();
        this.updateDigests(result, startPath);
        return result;
    }

    @Override
    public void updateDigests(final DirHasherResult digests, final File file) {
        final int numHashers =
            2 * Math.max(1, Runtime.getRuntime().availableProcessors() - NUM_REDUCERS - NUM_FILE_FEEDERS);
        final CountDownLatch onstop = new CountDownLatch(numHashers + NUM_REDUCERS + NUM_FILE_FEEDERS);
        final Disposable dispose = new Disposable()
        {
            @Override
            public void dispose() {
                LOG.debug("That's one down!");
                onstop.countDown();
            }
        };

        final Fiber fileFeederFiber = fiberFactory.create();
        fileFeederFiber.add(dispose);
        final FileNameGenerator generator =
            new FileNameGenerator(channels.getPathChannel(), channels.getFilenameChannel(),
                channels.getPathStopChannel(), channels.getFilenameStopChannel(),
                fileFeederFiber);
        generator.start();

        for (int i = 0; i < numHashers; i++) {
            final Fiber hasherFiber = fiberFactory.create();
            hasherFiber.add(dispose);
            final HasherActor hasherActor =
                new HasherActor(getTheAlgorithms(),
                    channels.getFilenameChannel(), channels.getDigestChannel(),
                    channels.getFilenameStopChannel(), channels.getDigestStopChannel(),
                    hasherFiber);
            hasherActor.start();
        }

        final Fiber reducerFiber = fiberFactory.create();
        reducerFiber.add(dispose);
        final ReducerActor reducerActor =
            new ReducerActor(digests, channels.getDigestChannel(), channels.getDigestStopChannel(), reducerFiber);

        reducerActor.start();

        /*
         * Now provision the generator, so it starts doing stuff.
         */
        channels.getPathChannel().publish(file);
        channels.getPathStopChannel().publish(null);

        try {
            LOG.info("And now we wait");
            onstop.await();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
    }
}
