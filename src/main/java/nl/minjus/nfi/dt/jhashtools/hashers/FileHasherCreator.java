package nl.minjus.nfi.dt.jhashtools.hashers;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;


public class FileHasherCreator {

    public static ConcurrencyMode concurrencyMode = ConcurrencyMode.SINGLE;

    public static FileHasher createSerialHasher() {
        return new SerialFileHasher();
    }

    public static FileHasher createThreadedHasher() {
        return new ConcurrentFileHasher();
    }

    public static FileHasher createFileHasher(MessageDigest algorithm) throws NoSuchAlgorithmException {
        FileHasher hasher = null;
        if (concurrencyMode == ConcurrencyMode.SINGLE) {
            hasher = new SerialFileHasher(algorithm);
        } else {
            hasher = new ConcurrentFileHasher(algorithm);
        }
        return hasher;
    }

    public static FileHasher createFileHasher() {
        FileHasher hasher = null;
        if (concurrencyMode == ConcurrencyMode.SINGLE) {
            hasher = new SerialFileHasher();
        } else {
            hasher = new ConcurrentFileHasher();
        }
        return hasher;
    }

    public static FileHasher createFileHasher(Collection<MessageDigest> algorithms) {
        FileHasher hasher = null;
        if (concurrencyMode == ConcurrencyMode.SINGLE) {
            hasher = new SerialFileHasher(algorithms);
        } else {
            hasher = new ConcurrentFileHasher(algorithms);
        }
        return hasher;
    }

    public static DigestResult computeDigest(File file, String algorithm)
            throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        FileHasher hasher = createFileHasher(digest);
        return hasher.getDigest(file);
    }

    public static DigestResult computeDigest(File file, Collection<String> algorithms)
            throws IOException, NoSuchAlgorithmException {
        Collection<MessageDigest> digestAlgorithms = new ArrayList<MessageDigest>(algorithms.size());
        for (String algorithmName: algorithms) {
            digestAlgorithms.add(MessageDigest.getInstance(algorithmName));
        }
        FileHasher hasher = createFileHasher(digestAlgorithms);
        return hasher.getDigest(file);
    }

    public static DigestResult computeDigest(File file)
            throws IOException, NoSuchAlgorithmException {
        return computeDigest(file, "sha-256");
    }


}
