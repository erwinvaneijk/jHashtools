/*
 * Copyright (c) 2010 Erwin van Eijk <erwin.vaneijk@gmail.com>.
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

package nl.minjus.nfi.dt.jhashtools.hashers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class DigestMaskTest
{

    @Test
    public void testMaskDoesNotDependOnDefaultLocale() {
        final Locale defaultLocale = Locale.getDefault();
        try {
            // Turkish case mapping turns "i" into a dotless or dotted capital, breaking naive lookups.
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            final DigestMask digestMask = DigestMask.getInstance();

            final Short lowerCaseMask = digestMask.getMask("digestmasktest-whirlpool");
            final Short upperCaseMask = digestMask.getMask("DIGESTMASKTEST-WHIRLPOOL");

            assertThat(upperCaseMask).isEqualTo(lowerCaseMask);
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    @Test
    public void testRepeatedLookupsReturnTheSameMask() {
        final DigestMask digestMask = DigestMask.getInstance();

        final Short firstMask = digestMask.getMask("digestmasktest-repeated");

        for (int i = 0; i < 20; i++) {
            assertThat(digestMask.getMask("digestmasktest-repeated")).isEqualTo(firstMask);
            assertThat(digestMask.getMask("DIGESTMASKTEST-REPEATED")).isEqualTo(firstMask);
        }
    }
}
