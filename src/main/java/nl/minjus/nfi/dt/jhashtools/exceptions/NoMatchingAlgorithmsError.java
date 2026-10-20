/*
 * Copyright (c) 2010 Erwin van Eijk.
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

package nl.minjus.nfi.dt.jhashtools.exceptions;

/**
 * Exception to be thrown when the requested algorithm cannot be found.
 */
public class NoMatchingAlgorithmsError extends RuntimeException
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID = -6496500981728230325L;

    public NoMatchingAlgorithmsError()
    {
    }
}
