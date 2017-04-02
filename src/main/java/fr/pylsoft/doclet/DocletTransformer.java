package fr.pylsoft.doclet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Create class inherit from this interface to generate your own rapport.
 * <p>
 * Don't forget to put the '-t <yourTransformerClass>' when you call javadoc exe
 *
 * @author pylsoft
 */
public abstract class DocletTransformer {

    private String repertoireDeSortie;

    private final XPath xpath = XPathFactory.newInstance().newXPath();

    protected static class TAG_XML {
        public static final String RACINE = "JAVADOC";
        public static final String CLASSE = "CLASSE";
        public static final String FONCTION = "FONCTION";
        public static final String ANNOTATION = "ANNOTATION";
        public static final String COMMENTAIRE = "COMMENTAIRE";
        public static final String PARAM = "PARAM";
        public static final String PHRASE = "PHRASE";
        public static final String LIGNE = "LIGNE";
        public static final String TAG = "TAG";
        public static final String RESUME = "RESUME";
    }

    public static class ATTRIBUT_XML {
        public static final String VERSION = "docletVersion";
        public static final String DATE = "date";
        public static final String NOM = "nom";
        public static final String PHRASE = "phrase";
        public static final String VALUE = "value";
        public static final String TYPE = "type";
        public static final String NOM_PARAMETRE = "nomParametre";
        public static final String DEPRECATED = "Deprecated";
        public static final String NOMBRE_PHRASE = "nbPhrases";
    }

    protected void setRepertoireDeSortie(String repertoireDeSortie) {
        this.repertoireDeSortie = repertoireDeSortie;
    }

    protected String getRepertoireDeSortie() {
        return repertoireDeSortie;
    }

    /**
     * Permet de définir le nom du fichier de sortie
     *
     * @return le nom du fichier de sortie avec son extension
     */
    public abstract String getNomFichier();

    protected void genererCucumberDoc(Document document) throws DocletCucumberException {


        File fichier = new File(getRepertoireDeSortie() + getNomFichier());
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fichier));
             PrintWriter printer = new PrintWriter(bufferedWriter)) {
            genererCucumberDoc(document.getDocumentElement(), printer);
        } catch (IOException e) {
            throw new DocletCucumberException("Erreur durant la création du fichier : " + e.getMessage(), e);
        }
        System.out.println("Fichier Généré via " + this.getClass().getSimpleName() + " : " + getNomFichier() + " a été créé.");
    }

    /**
     * Cette méthode est a surcharger pour créer le fichier de sortie à partir du Document dom xml
     *
     * @param elementRacine - l'élément racine du flux XML contenant la cucumber doc
     * @param printer       - le printer pour écrire dans le fichier
     */
    public abstract void genererCucumberDoc(final Element elementRacine, final PrintWriter printer) throws DocletCucumberException;

    /**
     * Cette méthode permet de lancer une évaluation d'une expression Xpath sur le noeud courant
     *
     * @param expression  - l'expression Xpath
     * @param nodeCourant - le noeud Xml courant
     * @return la liste des Elements résultants de l'expression
     */
    public List<Element> evaluerExpressionXpath(final String expression, final Node nodeCourant) {
        List<Element> listeElements = new ArrayList<>();
        try {
            NodeList resultats = (NodeList) xpath.evaluate(expression, nodeCourant, XPathConstants.NODESET);
            for (int index = 0; index < resultats.getLength(); index++) {
                Node item = resultats.item(index);
                if (Element.class.isAssignableFrom(item.getClass())) {
                    listeElements.add(Element.class.cast(item));
                }
            }
        } catch (XPathExpressionException e) {
            System.out.println("ERREUR durant l'évalution de l'expression :" + expression);
        }
        return listeElements;
    }
}
