package nl.minjus.nfi.dt.jhashtools.hashers;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Exchanger;
import java.util.logging.Level;

/**
* Created by IntelliJ IDEA. User: eijk Date: May 22, 2010 Time: 10:27:10 AM To change this template use File | Settings
* | File Templates.
*/
class DigestComputerThread implements Callable<DigestResult>
{

    private Collection<MessageDigest> digests;
    private Exchanger<ByteBuffer> exchanger;

    public DigestComputerThread(final Collection<MessageDigest> digests, Exchanger<ByteBuffer> exchanger)
    {
        this.digests = digests;
        this.exchanger = exchanger;
    }

    public DigestResult call()
    {
        try {
            byte[] buf = new byte[AbstractFileHasher.BLOCK_READ_SIZE];
            ByteBuffer buffer = ByteBuffer.wrap(buf, 0, AbstractFileHasher.BLOCK_READ_SIZE);
            while (true) {
                buffer = exchanger.exchange(buffer);
                if (buffer == null) {
                    break;
                }
                for (MessageDigest digest : this.digests) {
                    digest.update(buffer);
                }
            }
            final DigestResult res = new DigestResult();
            for (MessageDigest digest : this.digests) {
                res.add(new Digest(digest.getAlgorithm(), digest.digest()));
            }
            return res;
        } catch (InterruptedException ex) {
            ConcurrentFileHasher.LOG.log(Level.SEVERE, "Execution was aborted.", ex);
            return null;
        }
    }
}
