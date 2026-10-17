package nl.minjus.nfi.dt.jhashtools.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class PersistenceProviderCreatorTest {

    @Test
    public void testCreateJson() {
        final PersistenceProvider provider = PersistenceProviderCreator.create(PersistenceStyle.JSON);
        assertThat(provider).isInstanceOf(JsonPersistenceProvider.class);
    }

    @Test
    public void testCreateOldStyle() {
        final PersistenceProvider provider = PersistenceProviderCreator.create(PersistenceStyle.OLDSTYLE);
        assertThat(provider).isInstanceOf(OldStylePersistenceProvider.class);
    }

    @Test
    public void testCreateXml() {
        try {
            PersistenceProviderCreator.create(PersistenceStyle.XML);
            fail("Not implemented, so should not work");
        } catch (final RuntimeException ex) {
            // pass!
        }
    }
}
