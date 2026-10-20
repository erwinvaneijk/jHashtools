package nl.minjus.nfi.dt.jhashtools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;

public class DirHasherResultTest
{

    @Test
    public void testIntersection() throws Exception {
        final DirHasherResult knownDigests = KnownDigests.getKnownResults();
        final DirHasherResult knownSha256Digests = knownDigests.getByAlgorithm("sha-256");
        assertThat(knownDigests).isNotEqualTo(knownSha256Digests);
        assertThat(knownSha256Digests).isNotEqualTo(knownDigests);
        final DirHasherResult intersected = knownDigests.intersect(knownSha256Digests);
        assertThat(intersected).isNotEqualTo(knownDigests);
        assertThat(intersected).isEqualTo(knownSha256Digests);
    }

    @Test
    public void testChangedAccessTimesContainsOnlyChangedFiles() {
        final FileTime earlier = FileTime.from(Instant.parse("2001-02-03T04:05:06Z"));
        final FileTime later = FileTime.from(Instant.parse("2002-03-04T05:06:07Z"));
        final DirHasherResult result = new DirHasherResult();
        result.putAccessTimes(new File("unchanged"), new AccessTimes(earlier, earlier));
        result.putAccessTimes(new File("changed"), new AccessTimes(earlier, later));

        assertThat(result.getAccessTimes()).hasSize(2);
        assertThat(result.getChangedAccessTimes()).containsOnlyKeys(new File("changed"));
    }

    @Test
    public void testAccessTimesAreReadOnly() {
        final FileTime time = FileTime.from(Instant.parse("2001-02-03T04:05:06Z"));
        final DirHasherResult result = new DirHasherResult();

        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> result.getAccessTimes().put(new File("file"), new AccessTimes(time, time)));
    }

    @Test
    public void testPutAllAndClearIncludeAccessTimes() {
        final FileTime time = FileTime.from(Instant.parse("2001-02-03T04:05:06Z"));
        final DirHasherResult source = new DirHasherResult();
        source.putAccessTimes(new File("file"), new AccessTimes(time, time));
        final DirHasherResult result = new DirHasherResult();

        result.putAll(source);
        assertThat(result.getAccessTimes()).containsOnlyKeys(new File("file"));

        result.clear();
        assertThat(result.getAccessTimes()).isEmpty();
    }

    @Test
    public void testAccessTimesDoNotAffectEquality() {
        final FileTime time = FileTime.from(Instant.parse("2001-02-03T04:05:06Z"));
        final DirHasherResult withAccessTimes = KnownDigests.getKnownResults();
        withAccessTimes.putAccessTimes(new File("file"), new AccessTimes(time, time));

        assertThat(withAccessTimes).isEqualTo(KnownDigests.getKnownResults());
    }
}
