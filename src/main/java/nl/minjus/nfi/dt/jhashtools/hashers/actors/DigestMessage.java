package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;

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

    public DigestMessage(final File file, final DigestResult digest)
    {
        this.digest = digest;
        this.file = file;
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
}
