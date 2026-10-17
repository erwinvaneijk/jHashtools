package nl.minjus.nfi.dt.jhashtools;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import nl.minjus.nfi.dt.jhashtools.hashers.ConcurrencyMode;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasherCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProviderCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;

/**
 * Verifies a directory against a digest file written in each supported style.
 */
public class DirHasherResultVerifierTest
{
    private static final List<String> ALGORITHMS = List.of("md5", "sha-256", "ripemd160");

    @TempDir
    public Path tempDir;

    @ParameterizedTest
    @EnumSource(value = PersistenceStyle.class, names = { "JSON", "OLDSTYLE" })
    public void testUnchangedFilesPassVerification(final PersistenceStyle style)
        throws IOException, NoSuchAlgorithmException, PersistenceException
    {
        final Path data = createData();
        final Path digestFile = writeDigests(data, style);

        final Verification verification = verify(data, digestFile, style);

        assertThat(verification.passed()).isTrue();
        assertThat(verification.report()).contains("*** PASSED VERIFICATION ***")
            .doesNotContain("There are differences.");
    }

    @ParameterizedTest
    @EnumSource(value = PersistenceStyle.class, names = { "JSON", "OLDSTYLE" })
    public void testDigestFileInsideVerifiedDirectoryPassesVerification(final PersistenceStyle style)
        throws IOException, NoSuchAlgorithmException, PersistenceException
    {
        final Path data = createData();
        final Path digestFile = writeDigests(data, style, data.resolve("digests.txt"));

        final Verification verification = verify(data, digestFile, style);

        assertThat(verification.passed()).isTrue();
        assertThat(verification.report()).contains("*** PASSED VERIFICATION ***")
            .doesNotContain("There are differences.");
    }

    @ParameterizedTest
    @EnumSource(value = PersistenceStyle.class, names = { "JSON", "OLDSTYLE" })
    public void testModifiedFileFailsVerification(final PersistenceStyle style)
        throws IOException, NoSuchAlgorithmException, PersistenceException
    {
        final Path data = createData();
        final Path digestFile = writeDigests(data, style);
        Files.writeString(data.resolve("a.txt"), "changed\n", StandardCharsets.UTF_8);

        final Verification verification = verify(data, digestFile, style);

        assertThat(verification.passed()).isFalse();
        assertThat(verification.report()).contains("There are differences.").contains("a.txt")
            .doesNotContain("PASSED");
    }

    @ParameterizedTest
    @EnumSource(value = PersistenceStyle.class, names = { "JSON", "OLDSTYLE" })
    public void testDeletedFileFailsVerification(final PersistenceStyle style)
        throws IOException, NoSuchAlgorithmException, PersistenceException
    {
        final Path data = createData();
        final Path digestFile = writeDigests(data, style);
        Files.delete(data.resolve("b.txt"));

        final Verification verification = verify(data, digestFile, style);

        assertThat(verification.passed()).isFalse();
        assertThat(verification.report()).contains("There are differences.").contains("b.txt")
            .doesNotContain("PASSED");
    }

    private Path createData() throws IOException {
        final Path data = Files.createDirectory(this.tempDir.resolve("data"));
        Files.writeString(data.resolve("a.txt"), "hello\n", StandardCharsets.UTF_8);
        Files.writeString(data.resolve("b.txt"), "world\n", StandardCharsets.UTF_8);
        return data;
    }

    /**
     * Writes the digests of <code>data</code> to a file outside that directory, closing it before it is read.
     */
    private Path writeDigests(final Path data, final PersistenceStyle style)
        throws IOException, NoSuchAlgorithmException, PersistenceException
    {
        return writeDigests(data, style, this.tempDir.resolve("digests.txt"));
    }

    /**
     * Writes the digests of <code>data</code> to <code>digestFile</code>, closing it before it is read.
     */
    private Path writeDigests(final Path data, final PersistenceStyle style, final Path digestFile)
        throws IOException, NoSuchAlgorithmException, PersistenceException
    {
        final DirectoryHasher hasher = DirectoryHasherCreator.create(ConcurrencyMode.MULTI_THREADING, ALGORITHMS);
        final DirHasherResult digests = hasher.getDigests(data.toFile());
        try (OutputStream out = Files.newOutputStream(digestFile)) {
            PersistenceProviderCreator.create(style).persist(out, digests);
        }
        return digestFile;
    }

    private Verification verify(final Path data, final Path digestFile, final PersistenceStyle style)
        throws IOException, NoSuchAlgorithmException, PersistenceException
    {
        final DirectoryHasher hasher = DirectoryHasherCreator.create(ConcurrencyMode.MULTI_THREADING, ALGORITHMS);
        final DirHasherResultVerifier verifier = new DirHasherResultVerifier(hasher, style);
        verifier.loadDigestsFromFile(digestFile.toString());
        verifier.generateDigests(new String[] { data.toString() });

        final StringWriter report = new StringWriter();
        final boolean passed;
        try (PrintWriter writer = new PrintWriter(report)) {
            passed = verifier.verify(writer);
        }
        return new Verification(passed, report.toString());
    }

    private record Verification(boolean passed, String report)
    {
    }
}
