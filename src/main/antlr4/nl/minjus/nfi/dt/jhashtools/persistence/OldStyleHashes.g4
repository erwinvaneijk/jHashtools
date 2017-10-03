grammar OldStyleHashes;

options {
	language = Java;
}
	
@parser::header {

import nl.minjus.nfi.dt.jhashtools.Digest;
}

@lexer::header {
 }

digest returns [Digest digestResult]
	:	MD5 value=digestValue { $digestResult = new Digest("md5", $value.digestString); }
	|	SHA1 value=digestValue { $digestResult = new Digest("sha-1", $value.digestString); }
	|	SHA256 value=digestValue { $digestResult = new Digest("sha-256", $value.digestString); }
	;
	
digestValue returns [String digestString]
@init {
	$digestString = "";
}
	:	value=HEX_DIGITS rest=digestValue { $digestString = $digestString + $value.text + $rest.digestString; }
	|	
	;

MD5	:	'MD5:';

SHA1	:	('SHA-1:'|'SHA:');

SHA256	:	('SHA-256:'|'SHA256:');

HEX_DIGITS
	:	('A'..'F'|'a'..'f'|'0'..'9')+;

WS	:	(' '|'\t'|'\r'|'\n')+ {skip();} ;
