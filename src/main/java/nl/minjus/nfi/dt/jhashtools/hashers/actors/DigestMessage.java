package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;

import nl.minjus.nfi.dt.jhashtools.AccessTimes;
import nl.minjus.nfi.dt.jhashtools.DigestResult;

/**
 * Transport for the Digests.
 *
 * @author Erwin van Eijk
 *
 */
public class DigestMessage
{

    private final File file;

    private final DigestResult digest;

    private final AccessTimes accessTimes;

    public DigestMessage(final File file, final DigestResult digest, final AccessTimes accessTimes)
    {
        this.digest = digest;
        this.file = file;
        this.accessTimes = accessTimes;
    }

    /**
     * Get the file in this message.
     *
     * @return the filename
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Get the digest of the file.
     *
     * @return a digest
     */
    public DigestResult getDigestResult() {
        return this.digest;
    }

    /**
     * Get the access times observed while hashing the file.
     *
     * @return the observed access times
     */
    public AccessTimes getAccessTimes() {
        return this.accessTimes;
    }
}
