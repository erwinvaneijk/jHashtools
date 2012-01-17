package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

/**
 * Transport for the Digests.
 *
 * @author Erwin van Eijk
 *
 */
public class DigestMessage implements Message
{

    private final File file;

    private final DigestResult digest;

    public DigestMessage(final File file, final DigestResult digest)
    {
        this.digest = digest;
        this.file = file;
    }

    @Override
    public boolean isStop() {
        return false;
    }

    public File getFile() {
        return this.file;
    }

    public DigestResult getDigestResult() {
        return this.digest;
    }
}
