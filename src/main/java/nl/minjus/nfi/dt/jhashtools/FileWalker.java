/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

/**
 * This class offers a 'walker' that will go through a directory and offer
 * the resulting files to one or more visitors.
 * @author kojak
 */
public class FileWalker {
    private FileFilter fileFilter;
    private final List<WalkerVisitor> visitors;
    private boolean abort = false;

    public FileWalker() {
        this.visitors = new LinkedList<WalkerVisitor>();
        this.fileFilter = new FileFilter() {

            public boolean accept(File pathname) {
                return true;
            }
        };
    }

    public void setFileFilter(FileFilter filter) {
        this.fileFilter = filter;
    }

    public void addWalkerVisitor(WalkerVisitor visitor) {
        this.visitors.add(visitor);
    }

    public final List<WalkerVisitor> getWalkerVisitors() {
        return this.visitors;
    }
    
    public boolean walk(File file) {
        if (! file.exists()) {
            return true; // not existing is no reason to abort.
                         // we might opt for raising an exception on which
                         // you might want to do a retry....
        }
        if (file.isFile()) {
            for (WalkerVisitor visitor: this.visitors) {
                if (! visitor.visit(file)) {
                    this.abort = true;
                }
                return ! this.abort;
            }
        } else if (file.isDirectory()) {
            for (File child: file.listFiles(this.fileFilter)) {
                if (! walk(child)) {
                    this.abort = true;
                    return ! this.abort;
                }
            }
        }
        return true;
    }
}
