package fr.pylsoft.doclet;

import java.util.HashMap;
import java.util.Map;

public final class Option {
	static final String NAME = "-name";
	static final String OUT = "-d";
	static final String XSL_TXT = "-xsltxt";
	static final String XSL_HTML = "-xslhtml";
	static final String XML = "-xml";
	static final String HTML = "-html";
	static final String TXT = "-txt";
	static final String PROJET = "-projet";
	static final String TRANSFORMERS = "-t";

	static final Map<String, Integer> OPTIONS_LENGTH = new HashMap<>();

	static {
		OPTIONS_LENGTH.put(OUT, 2);
		OPTIONS_LENGTH.put(NAME, 2);
		OPTIONS_LENGTH.put(XSL_HTML, 2);
		OPTIONS_LENGTH.put(XSL_TXT, 2);
		OPTIONS_LENGTH.put(XML, 1);
		OPTIONS_LENGTH.put(HTML, 1);
		OPTIONS_LENGTH.put(TXT, 1);
		OPTIONS_LENGTH.put(PROJET, 2);
		OPTIONS_LENGTH.put(TRANSFORMERS, 2);
	}
}
