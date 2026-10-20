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
    @ValueSource(strings = { "JSON", "Json" })
    public void testFindJsonIgnoringCase(final String style) {
        assertThat(PersistenceStyle.convert(style)).isEqualTo(PersistenceStyle.JSON);
    }

    @ParameterizedTest
    @ValueSource(strings = { "OLD", "Old" })
    public void testFindOldStyleIgnoringCase(final String style) {
        assertThat(PersistenceStyle.convert(style)).isEqualTo(PersistenceStyle.OLDSTYLE);
    }

    @ParameterizedTest
    @ValueSource(strings = { "case", "CASE", "Case" })
    public void testFindCaseIgnoringCase(final String style) {
        assertThat(PersistenceStyle.convert(style)).isEqualTo(PersistenceStyle.CASE);
    }

    @ParameterizedTest
    @ValueSource(strings = { "oldstyle", "xml", "OLDSTYLE", "" })
    public void testUnsupportedStyleIsRejected(final String style) {
        assertThatThrownBy(() -> PersistenceStyle.convert(style))
            .isInstanceOf(UnsupportedPersistenceStyleException.class)
            .hasMessage("Unsupported style '" + style + "', use one of: json, old, case");
    }
}
