/*
 */
package nl.minjus.nfi.dt.jhashtools;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author kojak
 */
public class FileHasher {

    public static final String DEFAULT_ALGORITHM = "sha-256";
    private List<MessageDigest> digests;

    public static DigestResult computeDigest(File file, String algorithm)
            throws FileNotFoundException, IOException {
        FileHasher hasher = new FileHasher(algorithm);
        return hasher.getDigest(file);
    }

    public static DigestResult computeDigest(File file, Collection<String> algorithms)
            throws FileNotFoundException, IOException {
        FileHasher hasher = new FileHasher(algorithms);
        return hasher.getDigest(file);
    }

    public static DigestResult computeDigest(File file)
            throws IOException, FileNotFoundException {
        return FileHasher.computeDigest(file, "sha-256");
    }

    public FileHasher(String algorithm) {
        try {
            this.digests = new ArrayList<MessageDigest>();
            this.digests.add(MessageDigest.getInstance(algorithm));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public FileHasher(Collection<String> algorithms) {
        try {
            this.digests = new ArrayList<MessageDigest>();
            for (String algorithm : algorithms) {
                this.digests.add(MessageDigest.getInstance(algorithm));
            }
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public DigestResult getDigest(File file) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File %s does not exist", file.toString()));
        }
        FileInputStream stream = new FileInputStream(file);
        try {
            byte[] buf = new byte[8192];
            int bytesRead = 0;
            while ((bytesRead = stream.read(buf, 0, 8192)) != -1) {
                for (MessageDigest digest : digests) {
                    digest.update(buf, 0, bytesRead);
                }
            }
        } catch (EOFException ex) {
            // pass
        } finally {
            stream.close();
        }

        DigestResult res = new DigestResult();
        for (MessageDigest digest : digests) {
            res.add(new Digest(digest.getAlgorithm(), digest.digest()));
        }
        return res;
    }
}
