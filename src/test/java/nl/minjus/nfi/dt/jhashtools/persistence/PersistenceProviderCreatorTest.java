package nl.minjus.nfi.dt.jhashtools.persistence;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PersistenceProviderCreatorTest {
    @Rule
    public ExpectedException _exception = ExpectedException.none();

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

    @Test
    public void testCreateBogus() {
        _exception.expect(IllegalArgumentException.class);
        PersistenceProviderCreator.create(PersistenceStyle.valueOf("Foo"));
    }
}
