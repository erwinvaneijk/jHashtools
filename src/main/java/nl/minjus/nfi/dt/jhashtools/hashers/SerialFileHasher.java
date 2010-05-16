package nl.minjus.nfi.dt.jhashtools.hashers;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Collection;

/**
 * This class computes digests of files without using multithreading.
 */
public class SerialFileHasher extends AbstractFileHasher {

    public SerialFileHasher() {
        super();
    }

    public SerialFileHasher(MessageDigest digest) {
        super(digest);
    }

    public SerialFileHasher(Collection<MessageDigest> digests) {
        super(digests);
    }


    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param file the File to compute the digests for.
     *
     * @return a DigestResult containing the result of the computation.
     *
     * @throws java.io.FileNotFoundException thrown when <c>file</c> doesn't exist.
     * @throws java.io.IOException           thrown when some IOException occurs.
     */
    @Override
    public DigestResult getDigest(File file) throws FileNotFoundException, IOException {
        if (! file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        FileInputStream inputStream = new FileInputStream(file);
        return getDigestSingle(inputStream);
    }


    /**
     * Compute the digest(s) for the contents of the file.
     *
     * @param stream the stream to read.
     * @return the resulting digests.
     * @throws java.io.IOException when things go wrong with the IO.
     */
    private DigestResult getDigestSingle(FileInputStream stream) throws IOException {
        int bytesRead = 0;
        byte[] buf = new byte[BLOCK_READ_SIZE];
        do {
            bytesRead = stream.read(buf, 0, BLOCK_READ_SIZE);
            if (bytesRead > 0) {
                for (MessageDigest digest: digests) {
                    digest.update(buf, 0, bytesRead);
                }
            }
        } while (bytesRead > 0);

        return finalizeDigestResult();
    }
}
