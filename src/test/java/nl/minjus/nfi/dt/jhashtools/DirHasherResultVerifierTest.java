package nl.minjus.nfi.dt.jhashtools;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import nl.minjus.nfi.dt.jhashtools.hashers.ConcurrencyMode;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasherCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProvider;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProviderCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;

import org.junit.Test;

public class DirHasherResultVerifierTest
{

    @Test
    public void testConstructor() {
        final DirectoryHasher directoryHasher = DirectoryHasherCreator.create(ConcurrencyMode.MULTI_THREADING);
        final DirHasherResultVerifier verifier = new DirHasherResultVerifier(directoryHasher, PersistenceStyle.JSON);
        verifier.generateDigests(new String[]{"testdata"});
        final DirHasherResult result = directoryHasher.getDigests(new File("testdata"));

        final PersistenceProvider provider = PersistenceProviderCreator.create(PersistenceStyle.JSON);

        FileOutputStream out;
        try {
            final File testfile = new File("testoutput-ignore.json");
            out = new FileOutputStream(testfile);
            provider.persist(out, result);
            verifier.loadDigestsFromFile(testfile.getCanonicalPath());
            final ByteArrayOutputStream anOutput = new ByteArrayOutputStream();
            verifier.verify(new PrintWriter(anOutput));
            assertThat(anOutput.size(), is(equalTo(0)));
            testfile.delete();

            verifier.setIgnoreCase(false);
            verifier.verify(new PrintWriter(anOutput));
            assertThat(anOutput.size(), is(equalTo(0)));
            testfile.delete();
        } catch (final FileNotFoundException e) {
            fail("Should not happen");
        } catch (final PersistenceException e) {
            fail("Should not happen");
        } catch (final IOException e) {
            fail("Should not happen");
        }
    }
}
