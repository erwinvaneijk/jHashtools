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

package nl.minjus.nfi.dt.jhashtools.utils;

import java.io.IOException;
import java.net.URI;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Use this class to get version information from the jar's manifest.
 *
 * @author Erwin van Eijk
 */
public class Version
{

    /**
     * Get the stored version number from the jar.
     *
     * @return a string with the version.
     */
    public static String getVersion() {
        final Class<Version> clazz = Version.class;
        final String className = clazz.getSimpleName();
        final String classFileName = className + ".class";
        final String pathToClass = clazz.getResource(classFileName).toString();

        final int mark = pathToClass.indexOf("!");
        final String pathToManifest = pathToClass.substring(0, mark + 1) + "/META-INF/MANIFEST.MF";
        try {
            final Manifest manifest = new Manifest(URI.create(pathToManifest).toURL().openStream());
            final Attributes attributes = manifest.getMainAttributes();
            return attributes.getValue("Implementation-Version") + "-"
                + attributes.getValue("Implementation-Build");
        } catch (final IOException | IllegalArgumentException ex) {
            return "1.2-unknown";
        }
    }
}
