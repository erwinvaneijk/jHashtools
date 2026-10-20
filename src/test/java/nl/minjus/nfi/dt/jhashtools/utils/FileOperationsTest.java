package nl.minjus.nfi.dt.jhashtools.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileOperationsTest
{
    @Test
    public void testSameFileNotTheSameAndNotExist() {
        File firstFile = new File("does-not-exist");
        File secondFile = new File("does-not-exist");
        assertThat(FileOperations.isSameFile(firstFile, secondFile)).isFalse();
    }

    @Test
    public void testSameFileTheSame() {
        File firstFile = new File("testdata/testfile1.bin");
        File secondFile = new File("testdata/testfile1.bin");
        assertThat(FileOperations.isSameFile(firstFile, secondFile)).isTrue();
    }

    @Test
    public void testSameFileDifferent() {
        File firstFile = new File("does-not-exist");
        File secondFile = new File("testdata/testfile1.bin");
        assertThat(FileOperations.isSameFile(firstFile, secondFile)).isFalse();
    }

    @Test
    public void testIsWithinForNewFileInsideDirectory(@TempDir final Path tempFolder) throws IOException {
        final Path evidence = Files.createDirectories(tempFolder.resolve("evidence/sub"));

        assertThat(FileOperations.isWithin(evidence.resolve("digests.json"), tempFolder.resolve("evidence")))
            .isTrue();
    }

    @Test
    public void testIsWithinForFileOutsideDirectory(@TempDir final Path tempFolder) throws IOException {
        final Path evidence = Files.createDirectory(tempFolder.resolve("evidence"));

        assertThat(FileOperations.isWithin(tempFolder.resolve("digests.json"), evidence)).isFalse();
    }

    @Test
    public void testIsWithinForSiblingWithCommonPrefix(@TempDir final Path tempFolder) throws IOException {
        final Path evidence = Files.createDirectory(tempFolder.resolve("evidence"));
        Files.createDirectory(tempFolder.resolve("evidence-out"));

        assertThat(FileOperations.isWithin(tempFolder.resolve("evidence-out/digests.json"), evidence)).isFalse();
    }

    @Test
    public void testIsWithinForTheInputFileItself(@TempDir final Path tempFolder) throws IOException {
        final Path evidence = Files.writeString(tempFolder.resolve("evidence.bin"), "content");

        assertThat(FileOperations.isWithin(evidence, evidence)).isTrue();
    }

    @Test
    public void testIsWithinResolvesRelativeAndDottedPaths(@TempDir final Path tempFolder) throws IOException {
        final Path evidence = Files.createDirectories(tempFolder.resolve("evidence/sub"));
        final Path dotted = evidence.resolve("../digests.json");

        assertThat(FileOperations.isWithin(dotted, tempFolder.resolve("evidence"))).isTrue();
        assertThat(FileOperations.isWithin(Path.of("digests.json"), Path.of("."))).isTrue();
    }

    @Test
    public void testIsWithinFollowsSymbolicLinksIntoDirectory(@TempDir final Path tempFolder) throws IOException {
        assumeThat(OsUtils.isWindows()).as("symbolic links need privileges on Windows").isFalse();
        final Path evidence = Files.createDirectory(tempFolder.resolve("evidence"));
        final Path link = Files.createSymbolicLink(tempFolder.resolve("link"), evidence);

        assertThat(FileOperations.isWithin(link.resolve("digests.json"), evidence)).isTrue();
    }

    @Test
    public void testIsWithinForMissingDirectory(@TempDir final Path tempFolder) throws IOException {
        assertThat(FileOperations.isWithin(tempFolder.resolve("missing/digests.json"),
            tempFolder.resolve("missing"))).isFalse();
    }
}
