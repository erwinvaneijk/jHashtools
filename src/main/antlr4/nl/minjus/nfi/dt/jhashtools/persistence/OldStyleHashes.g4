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

grammar OldStyleHashes;

options {
	language = Java;
}
	
@parser::header {

import nl.minjus.nfi.dt.jhashtools.Digest;
}

@lexer::header {
 }

@parser::members {
	/**
	 * Converts a label such as "SHA-256:" to an algorithm name. Older files use "SHA:" and "SHA256:".
	 */
	private static String algorithmName(final String label) {
		final String name = label.substring(0, label.length() - 1).toLowerCase(java.util.Locale.ROOT);
		switch (name) {
			case "sha":
				return "sha-1";
			case "sha256":
				return "sha-256";
			default:
				return name;
		}
	}
}

digest returns [Digest digestResult]
	:	label=ALGORITHM value=digestValue EOF { $digestResult = new Digest(algorithmName($label.text), $value.digestString); }
	;
	
digestValue returns [String digestString]
@init {
	$digestString = "";
}
	:	value=HEX_DIGITS rest=digestValue { $digestString = $digestString + $value.text + $rest.digestString; }
	|	
	;

// An algorithm label as written by the old-style writer, e.g. "md5:", "sha-512:" or the legacy "SHA256:".
ALGORITHM
	:	('A'..'Z'|'a'..'z') ('A'..'Z'|'a'..'z'|'0'..'9'|'-')* ':';

HEX_DIGITS
	:	('A'..'F'|'a'..'f'|'0'..'9')+;

WS	:	(' '|'\t'|'\r'|'\n')+ {skip();} ;
