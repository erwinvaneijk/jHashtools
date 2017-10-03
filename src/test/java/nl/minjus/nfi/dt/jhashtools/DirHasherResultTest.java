package nl.minjus.nfi.dt.jhashtools;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import nl.minjus.nfi.dt.jhashtools.utils.KnownDigests;

public class DirHasherResultTest {

    @Test
    public void testIntersection() throws Exception {
        final DirHasherResult knownDigests = KnownDigests.getKnownResults();
        final DirHasherResult knownSha256Digests = knownDigests.getByAlgorithm("sha-256");
        assertThat(knownDigests, is(not(equalTo(knownSha256Digests))));
        assertThat(knownSha256Digests, is(not(equalTo(knownDigests))));
        final DirHasherResult intersected = knownDigests.intersect(knownSha256Digests);
        assertThat(intersected, is(not(equalTo(knownDigests))));
        assertThat(intersected, is(equalTo(knownSha256Digests)));
    }
}
