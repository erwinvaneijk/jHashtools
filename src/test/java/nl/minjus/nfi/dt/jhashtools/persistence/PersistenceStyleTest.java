package nl.minjus.nfi.dt.jhashtools.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nl.minjus.nfi.dt.jhashtools.exceptions.UnsupportedPersistenceStyleException;

public class PersistenceStyleTest
{
    @Test
    public void testFindJson() {
        PersistenceStyle expected = PersistenceStyle.convert("json");
        assertThat(expected).isEqualTo(PersistenceStyle.JSON);
    }

    @Test
    public void testFindOldStyle() {
        PersistenceStyle expected = PersistenceStyle.convert("old");
        assertThat(expected).isEqualTo(PersistenceStyle.OLDSTYLE);
    }

    @ParameterizedTest
    @ValueSource(strings = { "oldstyle", "xml", "JSON", "" })
    public void testUnsupportedStyleIsRejected(final String style) {
        assertThatThrownBy(() -> PersistenceStyle.convert(style))
            .isInstanceOf(UnsupportedPersistenceStyleException.class)
            .hasMessage("Unsupported style '" + style + "', use one of: json, old");
    }
}
