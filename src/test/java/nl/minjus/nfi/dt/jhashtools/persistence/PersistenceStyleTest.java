package nl.minjus.nfi.dt.jhashtools.persistence;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

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
