package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;

import kilim.Generator;
import kilim.Pausable;

/**
 * Generate <code>File</code> objects to use for further processing.
 *
 * @author Erwin van Eijk
 */
public class FileNameGenerator extends Generator<File>
{

    private final File startPath;

    public FileNameGenerator(final File startPath)
    {
        this.startPath = startPath;
    }

    @Override
    public void execute() throws Pausable {
        walkTheFile(this.startPath);
    }

    private void walkTheFile(final File aPath) throws Pausable {
        if (!aPath.exists()) {
            return;
        }
        if (aPath.isFile()) {
            yield(aPath);
        } else if (aPath.isDirectory()) {
            for (final File child : aPath.listFiles()) {
                if (child.isFile()) {
                    yield(child);
                } else {
                    walkTheFile(child);
                }
            }
        }
    }
}
