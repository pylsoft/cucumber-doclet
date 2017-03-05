package fr.pylsoft.doclet;

import java.util.HashMap;
import java.util.Map;

public final class Option {
	public static final String NAME = "-name";
	public static final String OUT = "-d";
	public static final String XSL_TXT = "-xsltxt";
	public static final String XSL_HTML = "-xslhtml";
	public static final String XML = "-xml";
	public static final String HTML = "-html";
	public static final String TXT = "-txt";
	public static final String TRANSFORMERS = "-t";

	public static final Map<String, Integer> OPTIONS_LENGTH = new HashMap<>();

	static {
		OPTIONS_LENGTH.put(OUT, 2);
		OPTIONS_LENGTH.put(NAME, 2);
		OPTIONS_LENGTH.put(XSL_HTML, 2);
		OPTIONS_LENGTH.put(XSL_TXT, 2);
		OPTIONS_LENGTH.put(XML, 1);
		OPTIONS_LENGTH.put(HTML, 1);
		OPTIONS_LENGTH.put(TXT, 1);
		OPTIONS_LENGTH.put(TRANSFORMERS, 2);
	}
}
