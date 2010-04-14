/*
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kojak
 */
public class FileHasher {

    public static final String DEFAULT_ALGORITHM = "sha-256";

    private MessageDigest digest;

    public static byte[] computeDigest(File file, String algorithm) 
            throws FileNotFoundException, IOException {
        FileHasher hasher = new FileHasher(algorithm);
        return hasher.getDigest(file);
    }

    public static byte[] computeDigest(File file) 
            throws IOException, FileNotFoundException {
        return FileHasher.computeDigest(file, "sha-256");
    }

    public FileHasher(String algorithm) {
        try {
            this.digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public byte[] getDigest(File file) throws FileNotFoundException, IOException {
        if (! file.exists()) {
            throw new FileNotFoundException(String.format("File %s does not exist", file.toString()));
        }
        FileInputStream stream = new FileInputStream(file);
        DigestInputStream dis = new DigestInputStream(stream, digest);
        dis.on(true);
        try {
            byte[] buf = new byte[8192];
            while ( dis.read(buf, 0, 8192) != -1) {
                // pass.
            }
        } catch (EOFException ex) {
            // pass
        } finally {
            stream.close();
        }

        return this.digest.digest();
    }
}
