package nl.minjus.nfi.dt.jhashtools;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import nl.minjus.nfi.dt.jhashtools.hashers.ConcurrencyMode;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasherCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;

public class DigestOutputCreatorTest
{
    @Test
    public void testConstructor() {
        try {
            final DirectoryHasher directoryHasher = DirectoryHasherCreator.create(ConcurrencyMode.MULTI_THREADING);
            final ByteArrayOutputStream out = new ByteArrayOutputStream();

            final DigestOutputCreator creator = new DigestOutputCreator(out, directoryHasher, false);
            creator.generate(new String[] { "testdata" });

            final File testfile = new File("testfile-ignore.json");
            if (testfile.exists()) {
                testfile.delete();
            }
            assertThat(testfile.exists(), is(false));
            creator.setOutputFile(testfile.getCanonicalPath());
            creator.setPersistenceStyle(PersistenceStyle.JSON);
            creator.finish();
            assertThat(testfile.exists(), is(true));
            testfile.delete();

            assertThat(out.size(), is(greaterThan(100)));

        } catch (final FileNotFoundException e) {
            fail("The file should have been created.");
        } catch (final IOException e) {
            fail("Should not happen");
        }

    }
}
