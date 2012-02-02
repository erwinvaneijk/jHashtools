package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import org.jetlang.channels.Channel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base for the various actors.
 *
 * @param <InType>
 *          The incoming type.
 * @param <OutType>
 *          The outgoing type.
 * @author Erwin van Eijk
 */
public abstract class Actor<InType, OutType>
{
    private static final Logger LOG = LoggerFactory.getLogger(Actor.class);

    private final Channel<InType> inChannel;
    private final Channel<OutType> outChannel;
    private final Channel<Void> stopChannel;
    private final Channel<Void> nextStopChannel;

    private final Fiber fiber;

    /**
     * Constructor.
     *
     * @param inChannel
     *            The channel on which the messages to be processed are delivered.
     * @param outChannel
     *            The channel where the resulting messages are pushed.
     * @param fiber
     *            The fiber that runs this actor.
     */
    public Actor(final Channel<InType> inChannel,
        final Channel<OutType> outChannel,
        final Channel<Void> stopChannel,
        final Channel<Void> nextStopChannel,
        final Fiber fiber)
    {
        this.inChannel = inChannel;
        this.outChannel = outChannel;
        this.stopChannel = stopChannel;
        this.nextStopChannel = nextStopChannel;
        this.fiber = fiber;
    }

    /**
     * Starting point for the Actor.
     */
    public void start() {
        // set up subscription listener
        final Callback<InType> onRecieve = new Callback<InType>()
        {
            @Override
            public void onMessage(final InType message) {
                final OutType msg = act(message);
                if (outChannel != null) {
                    outChannel.publish(msg);
                }
            }
        };
        // subscribe to incoming channel
        inChannel.subscribe(fiber, onRecieve);

        final Callback<Void> onStop = new Callback<Void>() {
            @Override
            public void onMessage(final Void message) {
                LOG.debug("Kill signal found");
                if (nextStopChannel != null) {
                    nextStopChannel.publish(null);
                }
                fiber.dispose();
            }
        };
        stopChannel.subscribe(fiber, onStop);
        // start the fiber
        fiber.start();
    }

    /**
     * Implement this message to get some work done on request.
     *
     * @param request
     *            the request to process.
     * @return response the response to this <code>request</code>.
     */
    public abstract OutType act(InType request);

    /**
     * A user can use this method to stuff extra messages into the outbound queue, since the act() method assumes a
     * one-on-one relation between inbound and outbound messages.
     *
     * @param message
     *            the message to stuff into the oubound queue.
     */
    protected void yield(final OutType message) {
        this.outChannel.publish(message);
    }
}
