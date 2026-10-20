/*
 * Copyright (c) 2012 Erwin van Eijk <erwin.vaneijk@gmail.com>.
 *
 * Licensed under the EUPL, Version 1.2 only (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific language
 * governing permissions and limitations under the Licence.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

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
