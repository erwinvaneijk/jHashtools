package nl.minjus.nfi.dt.jhashtools.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class PersistenceStyleTest {
    @Test
    public void testFindXml() {
        PersistenceStyle xml = PersistenceStyle.XML;
        assertThat(PersistenceStyle.convert("xml"), is(equalTo(xml)));
    }
    
    @Test
    public void testFindJson() {
        PersistenceStyle expected = PersistenceStyle.convert("json");
        assertThat(expected, is(equalTo(PersistenceStyle.JSON)));
    }

    @Test
    public void testFindOldStyle() {
        PersistenceStyle expected = PersistenceStyle.convert("old");
        assertThat(expected, is(equalTo(PersistenceStyle.OLDSTYLE)));
    }
}
