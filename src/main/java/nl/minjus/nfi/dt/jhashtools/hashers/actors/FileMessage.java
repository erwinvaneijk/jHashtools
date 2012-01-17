package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;

/**
 * This message is used to move File-objects around the message queues.
 *
 * @author Erwin van Eijk
 *
 */
public class FileMessage implements Message
{
    private final File file;

    private boolean stop;

    /**
     * Constructor.
     *
     * @param file
     *          the file to transport.
     */
    public FileMessage(final File file)
    {
        this.file = file;
        this.stop = false;
    }

    /**
     * True if the user of the message should stop processing further
     * messages.
     *
     * @return true
     *          if the user should stop processing further messages.
     */
    @Override
    public boolean isStop() {
        return this.stop;
    }

    /**
     * Set the <code>stop</code> trait of this message.
     *
     * @param stop
     */
    public void setStop(final boolean stop) {
        this.stop = stop;
    }

    public File getFile() {
        return this.file;
    }
}
