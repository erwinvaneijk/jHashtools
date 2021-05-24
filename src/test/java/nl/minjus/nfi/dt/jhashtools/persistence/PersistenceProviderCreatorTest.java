package nl.minjus.nfi.dt.jhashtools.persistence;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class PersistenceProviderCreatorTest {

    @Test
    public void testCreateJson() {
        final PersistenceProvider provider = PersistenceProviderCreator.create(PersistenceStyle.JSON);
        assertThat(provider, is(instanceOf(JsonPersistenceProvider.class)));
    }

    @Test
    public void testCreateOldStyle() {
        final PersistenceProvider provider = PersistenceProviderCreator.create(PersistenceStyle.OLDSTYLE);
        assertThat(provider, is(instanceOf(OldStylePersistenceProvider.class)));
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
