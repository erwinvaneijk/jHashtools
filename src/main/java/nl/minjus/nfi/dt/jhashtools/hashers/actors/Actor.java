package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import org.jetlang.channels.Channel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;

/**
 * The base for the various actors.
 * 
 * @author Erwin van Eijk
 */
public abstract class Actor {
	private Channel<Message> inChannel;
	private Channel<Message> outChannel;
	private Fiber fiber;

	/**
	 * Constructor.
	 * 
	 * @param inChannel
	 * 			 The channel on which the messages to be processed are delivered.
	 * @param outChannel
	 * 			 The channel where the resulting messages are pushed.
	 * @param fiber
	 * 			 The fiber that runs this actor.
	 */
	public Actor(Channel<Message> inChannel, Channel<Message> outChannel,	Fiber fiber) {
		this.inChannel = inChannel;
		this.outChannel = outChannel;
		this.fiber = fiber;
	}

	/**
	 * Starting point for the Actor.
	 */
	public void start() {
		// set up subscription listener
		Callback<Message> onRecieve = new Callback<Message>() {
			public void onMessage(Message message) {
				Message msg = act(message);
				if (outChannel != null) {
					outChannel.publish(msg);
				}
				// process poison pill, dispose current actor and pass the
				// message
				// on to the next actor in the chain (above)
				if (message == null || message.isStop()) {
					fiber.dispose();
				}
			}
		};
		// subscribe to incoming channel
		inChannel.subscribe(fiber, onRecieve);
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
	public abstract Message act(Message request);
	

	/**
	 * A user can use this method to stuff extra messages into the outbound
	 * queue, since the act() method assumes a one-on-one relation between
	 * inbound and outbound messages.
	 *  
	 * @param message the message to stuff into the oubound queue.
	 */
	protected void yield(Message message) {
		this.outChannel.publish(message);
	}
}
