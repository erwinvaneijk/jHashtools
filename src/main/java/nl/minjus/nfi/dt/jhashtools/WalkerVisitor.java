/*
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;

/**
 * WalkerVisitor instances are used when a file is visited while walking a file
 * tree.
 * 
 * @author Erwin J. van Eijk
 */
public interface WalkerVisitor {

    /**
     * Called when a file is visited.
     *
     * @param theFile
     * @return false if the walker should abort.
     */
    void visit(File theFile);

}
