package fr.pylsoft.doclet;

class DocletCucumberException extends Exception {

	private static final long serialVersionUID = 376217411923312463L;

	DocletCucumberException(String message) {
		super(message);
		
	}

	DocletCucumberException(String message, Exception e) {
		super(message, e);
	}

}
