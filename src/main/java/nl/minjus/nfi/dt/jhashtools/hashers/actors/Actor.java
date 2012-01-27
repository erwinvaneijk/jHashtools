package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import kilim.Mailbox;
import kilim.Pausable;
import kilim.Task;

/**
 * The base for the various actors.
 *
 * @author Erwin van Eijk
 */
public abstract class Actor extends Task
{
    private final Mailbox<Message> inbox;
    private final Mailbox<Message> outbox;

    public Actor(final int numThreads, final Mailbox<Message> inbox, final Mailbox<Message> outbox)
    {
        this.inbox = inbox;
        this.outbox = outbox;
    }

    @Override
    public void execute() throws Pausable {
        for (;;) {
            final Message request = inbox.get();
            if (request.isStop()) {
                if (outbox != null) {
                    outbox.put(request);
                }
                break;
            }
            final Message response = act(request);
            if (outbox != null) {
                outbox.put(response);
            }
        }
    }

    protected Mailbox<Message> getOutbox() {
    	return this.outbox;
    }
    
    protected Mailbox<Message> getInbox() {
    	return this.inbox;
    }
    
    /**
     * Implement this message to get some work done on request.
     *
     * @param request
     *          the request to process.
     * @return response
     *          the response to this <code>request</code>.
     */
    public abstract Message act(Message request);
}
