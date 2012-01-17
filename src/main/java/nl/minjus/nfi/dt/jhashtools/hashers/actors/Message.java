package nl.minjus.nfi.dt.jhashtools.hashers.actors;

/**
 * The standard interface for stuff that can be put into
 * mailboxes.
 *
 * @author Erwin van Eijk
 *
 */
public interface Message
{

    /**
     * This should indicate if this message is really supposed to be
     * a 'stop' message.
     *
     * @return true if the actor should stop after processing this
     *          message.
     */
    boolean isStop();
}
