package nl.minjus.nfi.dt.jhashtools;

import static org.assertj.core.api.Assertions.assertThat;

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
}
