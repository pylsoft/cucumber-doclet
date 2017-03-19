package fr.pylsoft.doclet;

public class DocletCucumberException extends Exception {

	private static final long serialVersionUID = 376217411923312463L;

	public DocletCucumberException(String message) {
		super(message);
		
	}

	public DocletCucumberException(String message, Exception e) {
		super(message, e);
	}

}
