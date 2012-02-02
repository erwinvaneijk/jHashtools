package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.util.logging.Logger;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;

/**
 * An actor that can reduce all the computed digests into one
 * single <code>DirHasherResult</code>.
 *
 * @author Erwin van Eijk
 *
 */
public class ReducerActor extends Actor
{
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(ReducerActor.class.getName());

    private final DirHasherResult dirHasherResult;

    public ReducerActor(final DirHasherResult dirHasherResult, final Channel<Message> inbox, Fiber fiber)
    {
        super(inbox, null, fiber);
        this.dirHasherResult = dirHasherResult;
    }

    @Override
    public Message act(final Message request) {
        final DigestMessage res = (DigestMessage) request;
        if (res != null) {
            dirHasherResult.put(res.getFile(), res.getDigestResult());
        }
        return null;
    }

}
