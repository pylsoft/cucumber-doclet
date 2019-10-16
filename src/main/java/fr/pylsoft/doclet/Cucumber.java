package fr.pylsoft.doclet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.javadoc.Main;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Cucumber {
    private static final Pattern SAUT_DE_LIGNE = Pattern.compile("\\n");

    private static final String DEPRECATED = "Deprecated";
    private static final String EXAMPLE = "example";

    private static final List<String> ANNOTATIONS_INCLUSES = new ArrayList<>();

    private static final Map<String, Integer> MAP_ANNOTATIONS_TROUVEES = new HashMap<>();

	static class TAG_XML {
        static final String RACINE = "JAVADOC";
        static final String CLASSE = "CLASSE";
        static final String FONCTION = "FONCTION";
        static final String ANNOTATION = "ANNOTATION";
        static final String COMMENTAIRE = "COMMENTAIRE";
        static final String PARAM = "PARAM";
        static final String PHRASE = "PHRASE";
        static final String LIGNE = "LIGNE";
        static final String TAG = "TAG";
        static final String RESUME = "RESUME";
    }

    static class ATTRIBUT_XML {
		public static final String PROJET = "projet";
		static final String VERSION = "docletVersion";
        static final String DATE = "date";
        static final String NOM = "nom";
        static final String PHRASE = "phrase";
        static final String VALUE = "value";
        static final String TYPE = "type";
        static final String NOM_PARAMETRE = "nomParametre";
        static final String DEPRECATED = "Deprecated";
        static final String NOMBRE_PHRASE = "nbPhrases";
    }

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            Main.execute(args);
        } else {
            Main.execute(new String[]{ //
                "-doclet", Cucumber.class.getName(), //
                "-classpath", "C:\\Users\\pylpy\\.m2\\repository\\info\\cukes\\cucumber-java\\1.2.5\\cucumber-java-1.2.5.jar", //
                "-docletpath", ".", //
                "-encoding", "UTF-8", //
                "-sourcepath", "D:\\Travail\\eclipse_Workspace\\prestation\\src\\test\\java", //
                Option.XSL_HTML, "D:\\Travail\\eclipse_Workspace\\prestation\\doc\\DocCucumberToHtml.xsl", //
                Option.XSL_TXT, "D:\\Travail\\eclipse_Workspace\\prestation\\doc\\DocCucumberToTexte.xsl", //
                Option.XML, //
                Option.HTML, //
                Option.TXT, //
                "fr.cnieg.sirius.prestation" //
            });
        }
    }

    public static int optionLength(String option) {
        Integer length = Option.OPTIONS_LENGTH.get(option);
        return length != null ? length : 0;
    }

    /**
     * @param root Document racine contenant le résultat du traitement réalisé par javadoc.exe
     * @return true si traitement ok sinon false
     */
    public static boolean start(RootDoc root) {
        try {
            ANNOTATIONS_INCLUSES.addAll(Util.recupererListeAnnotationsCucumber());
            ANNOTATIONS_INCLUSES.addAll(Arrays.asList(DEPRECATED));

            // ne pas oublier l'annotation @Deprecated

            creationDocumentXml(root);
        } catch (DocletCucumberException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    private static void creationDocumentXml(final RootDoc root) throws DocletCucumberException {

        Map<String, String> listeOptions = new HashMap<>();
        // Mise à jour des options
        for (final String[] option : root.options()) {
            listeOptions.put(option[0], option.length > 1 ? option[1] : "");
            System.out.println("option : " + option[0] + "/ valeur = " + (option.length > 1 ? option[1] : ""));
        }

        System.out.println("root.commentText =" + root.commentText());

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element elementRacine = document.createElement(TAG_XML.RACINE);
            elementRacine.setAttribute(ATTRIBUT_XML.DATE, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
            elementRacine.setAttribute(ATTRIBUT_XML.VERSION, Cucumber.class.getPackage().getImplementationVersion());
            if (listeOptions.containsKey(Option.PROJET)) {
				elementRacine.setAttribute(ATTRIBUT_XML.PROJET, listeOptions.get(Option.PROJET));
			}
            document.appendChild(elementRacine);

            ClassDoc[] classes = root.classes();
            for (final ClassDoc classeDoc : classes) {
                Element elm = docParClasse(document, classeDoc);
                if (elm != null) {
                    elementRacine.appendChild(elm);
                }
            }

            renseignerAnnotationsTrouveesDansDocument(document, elementRacine);

            creerFichiersSorties(listeOptions, document);
        } catch (ParserConfigurationException e) {
            throw new DocletCucumberException("Erreur durant la récupération de la configuration du Doclet", e);
        }
    }

    private static void renseignerAnnotationsTrouveesDansDocument(final Document document, final Element elementRacine) {
        if (MAP_ANNOTATIONS_TROUVEES.isEmpty()) {
            return;
        }

        Element elementResume = document.createElement(TAG_XML.RESUME);
        elementRacine.appendChild(elementResume);

        MAP_ANNOTATIONS_TROUVEES.forEach((annotation, nombre) -> {
            System.out.println(annotation + "=" + nombre + " phrases");
            Element elementAnnotation = document.createElement(TAG_XML.ANNOTATION);
            elementAnnotation.setAttribute(ATTRIBUT_XML.NOM, annotation);
            elementAnnotation.setAttribute(ATTRIBUT_XML.NOMBRE_PHRASE, nombre.toString());
            elementResume.appendChild(elementAnnotation);
        });
    }

    private static void ajouterNouvellePhraseDansMapAnnotation(final String nomAnnotation) {
        Integer nombre = MAP_ANNOTATIONS_TROUVEES.get(nomAnnotation);
        if (nombre == null) {
            nombre = 1;
        } else {
            nombre = Integer.sum(nombre, 1);
        }
        MAP_ANNOTATIONS_TROUVEES.put(nomAnnotation, nombre);
    }

    private static void creerFichiersSorties(final Map<String, String> listeOptions, final Document document) throws DocletCucumberException {

        try {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformerXML = transformerFactory.newTransformer();

            final boolean sortieHtml = listeOptions.containsKey(Option.HTML);
            final boolean sortieTxt = listeOptions.containsKey(Option.TXT);
            final boolean sortieXml = listeOptions.containsKey(Option.XML);

            final DOMSource source = new DOMSource(document);

            String nomFichierSortie = listeOptions.get(Option.NAME);
            if (Util.isNullOrEmpty(nomFichierSortie)) {
                nomFichierSortie = "JavadocCucumber";
            }
            String cheminComplet = listeOptions.get(Option.OUT);
            if (cheminComplet == null) {
                cheminComplet = "";
            }

            if (sortieXml || (!sortieHtml && !sortieTxt)) {
                Path pathFichierXml = Paths.get(cheminComplet, nomFichierSortie + ".xml");
                final StreamResult sortieXML = new StreamResult(pathFichierXml.toFile());
                transformerXML.transform(source, sortieXML);
                System.out.println("Fichier '" + pathFichierXml + "' créé.");
            }

            if (sortieHtml) {
                String cheminCompletXslVersHtml = listeOptions.get(Option.XSL_HTML);
                StreamSource stylesource;
                if (Util.isNullOrEmpty(cheminCompletXslVersHtml)) {
                    URL url = Cucumber.class.getClassLoader().getResource("doc/DocCucumberToHtml.xsl");
                    if (url == null) {
                    	throw new DocletCucumberException("impossible de trouver le fichier interne DocCucumberToHtml.xsl");
					}
                    stylesource = new StreamSource(url.openStream());
                } else {
                    stylesource = new StreamSource(new File(cheminCompletXslVersHtml));
                }

                final Transformer transformerHTML = transformerFactory.newTransformer(stylesource);
                final Path pathfichierHtml = Paths.get(cheminComplet, nomFichierSortie + ".html");
                final StreamResult sortieHTML = new StreamResult(pathfichierHtml.toFile());
                transformerHTML.transform(source, sortieHTML);
                System.out.println("Fichier '" + pathfichierHtml + "' créé.");
            }
            if (sortieTxt) {
                StreamSource stylesource;
                String cheminCompletXslVersText = listeOptions.get(Option.XSL_TXT);
                if (Util.isNullOrEmpty(cheminCompletXslVersText)) {
                    URL url = Cucumber.class.getClassLoader().getResource("doc/DocCucumberToTexte.xsl");
					if (url == null) {
						throw new DocletCucumberException("impossible de trouver le fichier interne DocCucumberToTexte.xsl");
					}
                    stylesource = new StreamSource(url.openStream());
                } else {
                    stylesource = new StreamSource(new File(cheminCompletXslVersText));
                }

                final Transformer transformerTXT = transformerFactory.newTransformer(stylesource);
                final Path pathFichierTxt = Paths.get(cheminComplet, nomFichierSortie + ".txt");
                final StreamResult sortieTXT = new StreamResult(pathFichierTxt.toFile());
                transformerTXT.transform(source, sortieTXT);
                System.out.println("Fichier '" + pathFichierTxt + "' créé.");
            }
        } catch (final TransformerException exe) {
            throw new DocletCucumberException("Erreur la préparation du fichier de sortie", exe);
        } catch (final IOException exe) {
            throw new DocletCucumberException("Erreur lors de la lecture du fichier de transformation xslt", exe);
        }
    }

    private static Element docParClasse(final Document document, final ClassDoc classeDoc) {
        Element elmClasse = document.createElement(TAG_XML.CLASSE);
        elmClasse.setAttribute(ATTRIBUT_XML.NOM, classeDoc.name());

        Arrays.stream(classeDoc.methods()) //
            .map(methodDoc -> docParMethode(document, methodDoc)) //
            .filter(Objects::nonNull) //
            .forEach(elmClasse::appendChild);

        return elmClasse.getChildNodes().getLength() > 0 ? elmClasse : null;
    }

    private static Element docParMethode(final Document document, final MethodDoc method) {
        final Element elmMethode = document.createElement(TAG_XML.FONCTION);
        if (method.annotations() != null && method.annotations().length > 0) {
            elmMethode.setAttribute(ATTRIBUT_XML.NOM, method.name());

            Arrays.stream(method.annotations()) //
                .map(annotationDesc -> docParAnnotation(document, elmMethode, annotationDesc, method.parameters())) //
                .filter(Objects::nonNull) //
                .forEach(elmMethode::appendChild);

            if (elmMethode.getChildNodes().getLength() > 0) {
                docParParametre(document, elmMethode, method.parameters());
                docParCommentaire(document, elmMethode, method.commentText());
                docParParametreTag(document, elmMethode, method.paramTags());
                docParTag(document, elmMethode, method.tags(EXAMPLE));
                return elmMethode;
            }
        }

        return null;
    }

    private static void docParParametre(final Document document, final Element elmMethode, final Parameter[] parameters) {
        for (Parameter parameter : parameters) {
            Element elmTag = document.createElement(TAG_XML.PARAM);
            elmTag.setAttribute(ATTRIBUT_XML.NOM, parameter.name());
            elmTag.setAttribute(ATTRIBUT_XML.TYPE, parameter.typeName());
            elmMethode.appendChild(elmTag);
        }
    }

    private static Element docParCommentaire(final Document document, final Element elmMethode, final String commentaire) {
        if (Util.isNotNullAndNotEmpty(commentaire)) {
            Element elmCommentaire = document.createElement(TAG_XML.COMMENTAIRE);
            String[] lignesCommentaire = SAUT_DE_LIGNE.split(commentaire);
            for (final String ligne : lignesCommentaire) {
                Element elmLigne = document.createElement(TAG_XML.LIGNE);
                elmLigne.setTextContent(ligne.replace("\"", "\\\""));
                elmCommentaire.appendChild(elmLigne);
            }
            elmMethode.appendChild(elmCommentaire);
        }

        return elmMethode;
    }

    private static void docParParametreTag(final Document document, final Element elmMethode, final ParamTag[] paramTags) {
        for (ParamTag tag : paramTags) {
            Element elmTag = document.createElement(TAG_XML.TAG);
            elmTag.setAttribute(ATTRIBUT_XML.NOM, tag.name().replaceAll("@", ""));
            elmTag.setAttribute(ATTRIBUT_XML.NOM_PARAMETRE, tag.parameterName());

            String[] lignesCommentaireTag = SAUT_DE_LIGNE.split(tag.parameterComment());
            for (String ligne : lignesCommentaireTag) {
                Element elmLigne = document.createElement(TAG_XML.LIGNE);
                elmLigne.setTextContent(ligne);
                elmTag.appendChild(elmLigne);
            }
            elmMethode.appendChild(elmTag);
        }
    }

    private static void docParTag(final Document document, final Element elmMethode, final Tag[] tags) {
        for (final Tag tag : tags) {
            Element elmTag = document.createElement(TAG_XML.TAG);
            elmTag.setAttribute(ATTRIBUT_XML.NOM, tag.name().replace("@", ""));

            String[] lignesCommentaireTag = SAUT_DE_LIGNE.split(tag.text());
            for (final String ligne : lignesCommentaireTag) {
                Element elmLigne = document.createElement(TAG_XML.LIGNE);
                elmLigne.setTextContent(ligne);
                elmTag.appendChild(elmLigne);
            }
            elmMethode.appendChild(elmTag);
        }
    }

    private static Element docParAnnotation(final Document document, final Element elmFonction, final AnnotationDesc annotation,
        final Parameter[] parametres) {
        String nomAnnotation = annotation.annotationType().simpleTypeName();

        if (!ANNOTATIONS_INCLUSES.contains(nomAnnotation)) {
            return null;
        }
        if (Objects.equals(nomAnnotation, DEPRECATED)) {
            elmFonction.setAttribute(ATTRIBUT_XML.DEPRECATED, "true");
            ajouterNouvellePhraseDansMapAnnotation(DEPRECATED);
            return null;
        } else {
            final Element elmAnnotation = document.createElement(TAG_XML.ANNOTATION);
            elmAnnotation.setAttribute(ATTRIBUT_XML.NOM, nomAnnotation);
            Arrays.stream(annotation.elementValues()) //
                .map(Cucumber::docParContenuAnnotation).filter(Util::isNotNullAndNotEmpty) //
                .forEach(phrase -> {
                    phrase = phrase.replace("\"", "\\\"");
                    elmAnnotation.setAttribute(ATTRIBUT_XML.PHRASE, phrase);
                    creerListeChoixPhrase(document, elmAnnotation, phrase, parametres);
                });

            if (elmAnnotation.getAttribute(ATTRIBUT_XML.PHRASE) != null) {
                ajouterNouvellePhraseDansMapAnnotation(nomAnnotation);
                return elmAnnotation;
            }
            return null;
        }
    }

    private static void creerListeChoixPhrase(final Document document, final Element elmAnnotation, final String phrase, final Parameter[] parametres) {
        List<String> listePhrasesPossibles = Util.extraireListePhrases(phrase);
        if (!listePhrasesPossibles.isEmpty()) {
            for (final String phrasePossible : listePhrasesPossibles) {
                Element elm = document.createElement(TAG_XML.PHRASE);
                elm.setTextContent(Util.ajoutParametreDansPhrasePossible(phrasePossible, parametres));
                elmAnnotation.appendChild(elm);
            }
        }
    }

    private static String docParContenuAnnotation(final ElementValuePair elementValuePair) {
        String phrase = null;
        if (ATTRIBUT_XML.VALUE.equals(elementValuePair.element().name())) {
            AnnotationValue annotationValue = elementValuePair.value();
            if (annotationValue != null) {
                phrase = annotationValue.value().toString();
                phrase = phrase.replaceAll("[\\^$]", "");
            }
        }

        return phrase;
    }
}
