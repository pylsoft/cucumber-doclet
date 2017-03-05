package fr.pylsoft.doclet;

import org.w3c.dom.Document;

/**
 * Create class inherit from this interface to generate your own rapport.
 * 
 * Don't forget to put the '-t <yourTransformerClass>' when you call javadoc exe 
 * @author pylsoft
 */
public interface Transformer {

	boolean genererCucumberDoc(final String repertoireSortie, final String nomFichier, final Document document); 
}
