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

import org.junit.jupiter.api.Test;

public class PersistenceProviderCreatorTest
{

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
    public void testCreateCase() {
        final PersistenceProvider provider = PersistenceProviderCreator.create(PersistenceStyle.CASE);
        assertThat(provider).isInstanceOf(CasePersistenceProvider.class);
    }
}
