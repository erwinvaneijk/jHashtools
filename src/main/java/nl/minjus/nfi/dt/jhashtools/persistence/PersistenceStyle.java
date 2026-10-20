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

package nl.minjus.nfi.dt.jhashtools.persistence;

import nl.minjus.nfi.dt.jhashtools.exceptions.UnsupportedPersistenceStyleException;

/**
 * The definition of the various supported persistence styles.
 *
 * @author Erwin van Eijk
 */
public enum PersistenceStyle
{
    /**
     * JSON.
     */
    JSON,
    /**
     * The old style of outputting the results. Pretty horrible format, really.
     */
    OLDSTYLE,
    /**
     * CASE/UCO JSON-LD.
     */
    CASE;

    /**
     * Convert the description into the suitable enum member. The description is matched ignoring case.
     *
     * @param aDescription
     *            the description to convert.
     * @return a PersistenceStyle
     * @throws UnsupportedPersistenceStyleException
     *             if <code>aDescription</code> is not one of the supported styles.
     */
    public static PersistenceStyle convert(final String aDescription) {
        if ("json".equalsIgnoreCase(aDescription)) {
            return JSON;
        } else if ("old".equalsIgnoreCase(aDescription)) {
            return OLDSTYLE;
        } else if ("case".equalsIgnoreCase(aDescription)) {
            return CASE;
        } else {
            throw new UnsupportedPersistenceStyleException("Unsupported style '" + aDescription
                + "', use one of: json, old, case");
        }
    }
}
