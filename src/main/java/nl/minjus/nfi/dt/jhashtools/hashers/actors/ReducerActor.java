package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An actor that can reduce all the computed digests into one
 * single <code>DirHasherResult</code>.
 *
 * @author Erwin van Eijk
 *
 */
public class ReducerActor extends Actor<DigestMessage, Void>
{
    private static final Logger LOG = LoggerFactory.getLogger(ReducerActor.class.getName());

    private final DirHasherResult dirHasherResult;

    public ReducerActor(final DirHasherResult dirHasherResult,
        final Channel<DigestMessage> inbox,
        final Channel<Void> stopChannel,
        final Fiber fiber)
    {
        super(inbox, null, stopChannel, null, fiber);
        this.dirHasherResult = dirHasherResult;
    }

    @Override
    public Void act(final DigestMessage request) {
        if ((request != null) && (request.getFile() != null)) {
            LOG.debug("Got a message " + request.getFile());
            dirHasherResult.put(request.getFile(), request.getDigestResult());
            LOG.debug("#" + dirHasherResult.size());
        } else {
            LOG.debug("Null message");
        }
        return null;
    }

}
