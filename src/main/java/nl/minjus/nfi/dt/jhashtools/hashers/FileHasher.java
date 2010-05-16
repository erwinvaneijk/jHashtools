package nl.minjus.nfi.dt.jhashtools.hashers;

import nl.minjus.nfi.dt.jhashtools.DigestResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by IntelliJ IDEA. User: eijk Date: May 15, 2010 Time: 11:35:58 PM To change this template use File | Settings
 * | File Templates.
 */
public interface FileHasher {
    String DEFAULT_ALGORITHM = "sha-256";
    String NO_ALGORITHM = "none";

    void addAlgorithm(MessageDigest algorithm);

    DigestResult getDigest(File file) throws FileNotFoundException, IOException;

    void reset();
}
