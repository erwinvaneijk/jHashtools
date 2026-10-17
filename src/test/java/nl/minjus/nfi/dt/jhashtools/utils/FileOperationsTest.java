package nl.minjus.nfi.dt.jhashtools.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Test;

public class FileOperationsTest {
    @Test
    public void testSameFileNotTheSameAndNotExist() {
        File firstFile = new File("does-not-exist");
        File secondFile = new File("does-not-exist");
        assertThat(FileOperations.isSameFile(firstFile,secondFile)).isFalse();
    }

    @Test
    public void testSameFileTheSame() {
        File firstFile = new File("testdata/testfile1.bin");
        File secondFile = new File("testdata/testfile1.bin");
        assertThat(FileOperations.isSameFile(firstFile,secondFile)).isTrue();
    }

    @Test
    public void testSameFileDifferent() {
        File firstFile = new File("does-not-exist");
        File secondFile = new File("testdata/testfile1.bin");
        assertThat(FileOperations.isSameFile(firstFile,secondFile)).isFalse();
    }
}
