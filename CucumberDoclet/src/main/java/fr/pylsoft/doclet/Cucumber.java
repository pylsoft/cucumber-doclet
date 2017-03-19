package fr.pylsoft.doclet;

import java.io.File;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

public class Cucumber {
	final static String SAUT_DE_LIGNE = "\\n";

	final static public String DEPRECATED = "Deprecated";
	final static public String EXAMPLE = "Example";

	static List<String> ANNOTATIONS_INCLUSES = new ArrayList<>(); 
	
	final static Map<String,Integer> mapAnnotationsTrouvees = new HashMap<>();

	static class TAG_XML {
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

	static class ATTRIBUT_XML {
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

	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			Main.execute(args);
		} else {
			Main.execute(new String[] { //
					"-doclet", Cucumber.class.getName(), //
					"-classpath",
					"C:\\Users\\pylpy\\.m2\\repository\\info\\cukes\\cucumber-java\\1.2.5\\cucumber-java-1.2.5.jar", //
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
	 * @param root
	 * @return
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
		// Mise � jour des options
		for (String[] option : root.options()) {
			listeOptions.put(option[0], option.length > 1 ? option[1] : "");
		}

		
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element elementRacine = document.createElement(TAG_XML.RACINE);
			elementRacine.setAttribute(ATTRIBUT_XML.DATE, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
			elementRacine.setAttribute(ATTRIBUT_XML.VERSION,Cucumber.class.getPackage().getImplementationVersion());
			document.appendChild(elementRacine);

			ClassDoc[] classes = root.classes();
			for (ClassDoc classeDoc : classes) {
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

	private static void renseignerAnnotationsTrouveesDansDocument(Document document, Element elementRacine) {
		if (mapAnnotationsTrouvees.isEmpty()) {
			return;
		}

		Element elementResume = document.createElement(TAG_XML.RESUME);
		elementRacine.appendChild(elementResume);

		mapAnnotationsTrouvees.forEach((annotation, nombre) -> { 
			System.out.println(annotation  + "=" + nombre + " phrases");
			Element elementAnnotation = document.createElement(TAG_XML.ANNOTATION);
			elementAnnotation.setAttribute(ATTRIBUT_XML.NOM, annotation);
			elementAnnotation.setAttribute(ATTRIBUT_XML.NOMBRE_PHRASE, nombre.toString());			
			elementResume.appendChild(elementAnnotation);			
		});
		
	}
	
	private static void ajouterNouvellePhraseDansMapAnnotation(String nomAnnotation) {
		Integer nombre = mapAnnotationsTrouvees.get(nomAnnotation);
		if (nombre == null) {
			nombre = new Integer(1);
		} else {
			nombre = Integer.sum(nombre,1);
		}
		mapAnnotationsTrouvees.put(nomAnnotation, nombre);
	}

	private static void creerFichiersSorties(Map<String, String> listeOptions, Document document) throws DocletCucumberException {

		try {
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformerXML = transformerFactory.newTransformer();

			final boolean sortieHtml = listeOptions.containsKey(Option.HTML);
			final boolean sortieTxt = listeOptions.containsKey(Option.TXT);
			final boolean sortieXml = listeOptions.containsKey(Option.XML);

			String cheminCompletXslVersHtml = listeOptions.get(Option.XSL_HTML);
			if (Util.isNullOrEmpty(cheminCompletXslVersHtml)) {
				cheminCompletXslVersHtml = "DocCucumberToHtml.xsl";
			}
			String cheminCompletXslVersText = listeOptions.get(Option.XSL_TXT);
			if (Util.isNullOrEmpty(cheminCompletXslVersText)) {
				cheminCompletXslVersText = "DocCucumberToTxt.xsl";
			}

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
				StreamSource stylesource = new StreamSource(new File(cheminCompletXslVersHtml));
				final Transformer transformerHTML = transformerFactory.newTransformer(stylesource);
				final Path pathfichierHtml = Paths.get(cheminComplet, nomFichierSortie + ".html");
				final StreamResult sortieHTML = new StreamResult(pathfichierHtml.toFile());
				transformerHTML.transform(source, sortieHTML);
				System.out.println("Fichier '" + pathfichierHtml + "' créé.");
			}
			if (sortieTxt) {
				StreamSource stylesource = new StreamSource(new File(cheminCompletXslVersText));
				final Transformer transformerTXT = transformerFactory.newTransformer(stylesource);
				final Path pathFichierTxt = Paths.get(cheminComplet, nomFichierSortie + ".txt");
				final StreamResult sortieTXT = new StreamResult(pathFichierTxt.toFile());
				transformerTXT.transform(source, sortieTXT);
				System.out.println("Fichier '" + pathFichierTxt + "' créé.");
			}
		} catch (TransformerException e) {
			throw new DocletCucumberException("Erreur la préparation du fichier de sortie", e);
		}
	}

	private static Element docParClasse(Document document, ClassDoc classeDoc) {
		Element elmClasse = document.createElement(TAG_XML.CLASSE);
		elmClasse.setAttribute(ATTRIBUT_XML.NOM, classeDoc.name());

		Arrays.stream(classeDoc.methods()) //
				.map(methodDoc -> docParMethode(document, methodDoc)) //
				.filter(Objects::nonNull) //
				.forEach(elm -> elmClasse.appendChild(elm));

		return elmClasse.getChildNodes().getLength() > 0 ? elmClasse : null;
	}

	private static Element docParMethode(final Document document, final MethodDoc method) {
		final Element elmMethode = document.createElement(TAG_XML.FONCTION);
		if (method.annotations() != null && method.annotations().length > 0) {
			elmMethode.setAttribute(ATTRIBUT_XML.NOM, method.name());

			Arrays.stream(method.annotations()) //
					.map(annotationDesc -> docParAnnotation(document, elmMethode, annotationDesc, method.parameters())) //
					.filter(Objects::nonNull) //
					.forEach(elm -> elmMethode.appendChild(elm));

			if (elmMethode.getChildNodes().getLength() > 0) {
				docParParametre(document, elmMethode, method.parameters());
				docParCommentaire(document, elmMethode, method.commentText());
				docParParametreTag(document, elmMethode, method.paramTags());
				docParTag(document, elmMethode, method.tags("example"));
				return elmMethode;
			}
		}

		return null;
	}

	private static void docParParametre(Document document, Element elmMethode, Parameter[] parameters) {
		for (Parameter parameter : parameters) {
			Element elmTag = document.createElement(TAG_XML.PARAM);
			elmTag.setAttribute(ATTRIBUT_XML.NOM, parameter.name());
			elmTag.setAttribute(ATTRIBUT_XML.TYPE, parameter.typeName());
			elmMethode.appendChild(elmTag);
		}
	}

	private static Element docParCommentaire(Document document, final Element elmMethode, final String commentaire) {
		if (Util.isNotNullAndNotEmpty(commentaire)) {
			Element elmCommentaire = document.createElement(TAG_XML.COMMENTAIRE);
			String[] lignesCommentaire = commentaire.split(SAUT_DE_LIGNE);
			for (String ligne : lignesCommentaire) {
				Element elmLigne = document.createElement(TAG_XML.LIGNE);
				elmLigne.setTextContent(ligne.replace("\"", "\\\""));
				elmCommentaire.appendChild(elmLigne);
			}
			elmMethode.appendChild(elmCommentaire);
		}

		return elmMethode;
	}

	private static void docParParametreTag(Document document, Element elmMethode, ParamTag[] paramTags) {
		for (ParamTag tag : paramTags) {
			Element elmTag = document.createElement(TAG_XML.TAG);
			elmTag.setAttribute(ATTRIBUT_XML.NOM, tag.name().replaceAll("@", ""));
			elmTag.setAttribute(ATTRIBUT_XML.NOM_PARAMETRE, tag.parameterName());

			String[] lignesCommentaireTag = tag.parameterComment().split(SAUT_DE_LIGNE);
			for (String ligne : lignesCommentaireTag) {
				Element elmLigne = document.createElement(TAG_XML.LIGNE);
				elmLigne.setTextContent(ligne);
				elmTag.appendChild(elmLigne);
			}
			elmMethode.appendChild(elmTag);
		}
	}

	private static void docParTag(Document document, Element elmMethode, Tag[] tags) {
		for (Tag tag : tags) {
			Element elmTag = document.createElement(TAG_XML.TAG);
			elmTag.setAttribute(ATTRIBUT_XML.NOM, tag.name().replace("@", ""));

			String[] lignesCommentaireTag = tag.text().split(SAUT_DE_LIGNE);
			for (String ligne : lignesCommentaireTag) {
				Element elmLigne = document.createElement(TAG_XML.LIGNE);
				elmLigne.setTextContent(ligne);
				elmTag.appendChild(elmLigne);
			}
			elmMethode.appendChild(elmTag);
		}
	}

	private static Element docParAnnotation(final Document document, final Element elmFonction,
			final AnnotationDesc annotation, final Parameter[] parametres) {
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

	private static void creerListeChoixPhrase(Document document, Element elmAnnotation, String phrase,
			Parameter[] parametres) {
		List<String> listePhrasesPossibles = Util.extraireListePhrases(phrase);
		if (!listePhrasesPossibles.isEmpty()) {
			for (final String phrasePossible : listePhrasesPossibles) {
				Element elm = document.createElement(TAG_XML.PHRASE);
				elm.setTextContent(Util.ajoutParametreDansPhrasePossible(phrasePossible, parametres));
				elmAnnotation.appendChild(elm);
			}
		}
	}

	private static String docParContenuAnnotation(ElementValuePair elementValuePair) {
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
