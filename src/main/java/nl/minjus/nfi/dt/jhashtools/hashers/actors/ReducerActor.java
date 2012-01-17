package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.util.logging.Logger;

import kilim.Mailbox;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 * An actor that can reduce all the computed digests into one
 * single <code>DirHasherResult</code>.
 *
 * @author Erwin van Eijk
 *
 */
public class ReducerActor extends Actor
{
    private static final Logger LOG = Logger.getLogger(ReducerActor.class.getName());

    private final DirHasherResult dirHasherResult;

    public ReducerActor(final DirHasherResult dirHasherResult, final int numThreads, final Mailbox<Message> inbox)
    {
        super(numThreads, inbox, null);
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
