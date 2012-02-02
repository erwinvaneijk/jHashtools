package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;

/**
 * This class contains all the channels.
 *
 * @author Erwin van Eijk
 *
 */
public final class Channels
{
    private final Channel<File> filenameChannel = new MemoryChannel<File>();
    private final Channel<Void> filenameStopChannel = new MemoryChannel<Void>();
    private final Channel<DigestMessage> digestChannel = new MemoryChannel<DigestMessage>();
    private final Channel<Void> digestStopChannel = new MemoryChannel<Void>();
    private final Channel<File> pathChannel = new MemoryChannel<File>();
    private final Channel<Void> pathStopChannel = new MemoryChannel<Void>();

    /**
     * @return the filenameChannel
     */
    public Channel<File> getFilenameChannel() {
        return filenameChannel;
    }
    /**
     * @return the filenameStopChannel
     */
    public Channel<Void> getFilenameStopChannel() {
        return filenameStopChannel;
    }
    /**
     * @return the digestChannel
     */
    public Channel<DigestMessage> getDigestChannel() {
        return digestChannel;
    }
    /**
     * @return the digestStopChannel
     */
    public Channel<Void> getDigestStopChannel() {
        return digestStopChannel;
    }
    /**
     * @return the pathChannel
     */
    public Channel<File> getPathChannel() {
        return pathChannel;
    }
    /**
     * @return the pathStopChannel
     */
    public Channel<Void> getPathStopChannel() {
        return pathStopChannel;
    }
}
