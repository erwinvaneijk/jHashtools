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

import java.util.Arrays;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * Swallows the exceptions listed in a test method's {@link AllowedFailure} annotation.
 */
public class AllowedFailureExtension implements TestExecutionExceptionHandler
{

    @Override
    public void handleTestExecutionException(final ExtensionContext context, final Throwable throwable)
        throws Throwable
    {
        final boolean allowed = AnnotationSupport.findAnnotation(context.getElement(), AllowedFailure.class)
            .map(annotation -> Arrays.stream(annotation.value()).anyMatch(type -> type.isInstance(throwable)))
            .orElse(false);
        if (!allowed) {
            throw throwable;
        }
    }
}
