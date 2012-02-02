package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jetlang.channels.Channel;
import org.jetlang.core.Disposable;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;

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

	private static final int NUM_HASHERS = 1;

	private static final int NUM_REDUCERS = 1;

	private static final int NUM_FILE_FEEDERS = 1;
	
    private final PoolFiberFactory fiberFactory;

	private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    /**
     * Constructor.
     */
    public ActingDirectoryHasher()
    {
    	fiberFactory = new PoolFiberFactory(executorService);
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
        fiberFactory = new PoolFiberFactory(executorService);
    }

    @Override
    public DirHasherResult getDigests(final File startPath) {
        final DirHasherResult result = new DirHasherResult();
        this.updateDigests(result, startPath);
        return result;
    }

    @Override
    public void updateDigests(final DirHasherResult digests, final File file) {

    	final CountDownLatch onstop = new CountDownLatch(NUM_HASHERS + NUM_REDUCERS + NUM_FILE_FEEDERS);
        Disposable dispose = new Disposable() {
          public void dispose() {
            onstop.countDown();
          }
        };

        Fiber fileFeederFiber = fiberFactory.create();
        fileFeederFiber.add(dispose);
        FileNameGenerator generator = new FileNameGenerator(Channels.pathChannel, Channels.filenameChannel, fileFeederFiber);
        generator.start();
        
        for (int i=0;i<NUM_HASHERS;i++) {
        	Fiber hasherFiber = fiberFactory.create();
        	hasherFiber.add(dispose);
        	HasherActor hasherActor = new HasherActor(getTheAlgorithms(), Channels.filenameChannel, Channels.digestChannel, hasherFiber);
        	hasherActor.start();
        }
        
        Fiber reducerFiber = fiberFactory.create();
        reducerFiber.add(dispose);
        ReducerActor reducerActor = new ReducerActor(digests, Channels.digestChannel, reducerFiber);
        
        reducerActor.start();   

        /*
         * Now provision the generator, so it starts doing stuff.
         */
        Channels.pathChannel.publish(new FileMessage(file));
        
        try { 
            onstop.await(); 
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          executorService.shutdown();
    }
}
