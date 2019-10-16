package fr.pylsoft.doclet;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sun.javadoc.Parameter;

public class Util {

	static final Pattern pattern = Pattern.compile("^(.*)\\(\\?\\:([^\\)]*)\\)(.*)$");
	static final String NOM_PACKAGE_CUCUMBER = "cucumber.api.java";
	static final String PART_NOM_JAR_CUCUMBER = "cucumber-java";

	public static void main(String[] args) {
		 System.out.println("----------");
		 System.out.println("- Test 1 -");
		 System.out.println("----------");
		 String phrase = "le contexte (?:de|du|des|d'|le|la|les|l') (.*) de ta (?:mere|pere) (.*):";
		 //String phrase = "un b�n�ficiaire ouvrant-droit avec le matricule '(.*)' et le num�ro de famille '(.*)'";
		 System.out.println(phrase);
		 System.out.println("-------");
		 extraireListePhrases(phrase).forEach(System.out::println);
		
		 System.out.println("----------");
		 System.out.println("- Test 2 -");
		 System.out.println("----------");
		 String phraseApresTraitement = "un b�n�ficiaire : ouvrant-droit avec le matricule '(.*)' et le num�ro de famille '(.*)':";
		 System.out.println(phraseApresTraitement);
		 System.out.println("-------");
		 {
			 String [] parametres = new String[] {"PARAM1","PARAM2","PARAM3"};
			for (String parametre : parametres) {
				String nomParametre = "[" + parametre + "]";
				phraseApresTraitement = phraseApresTraitement.replaceFirst("\\([^\\)]*\\)|(:$)", "$1" + nomParametre);
			}

			System.out.println("phraseApresTraitement="+phraseApresTraitement);
		 }
		System.out.println("----------");
		System.out.println("- Test 3 -");
		System.out.println("----------");
		try {
			List<String> listeAnnotations = recupererListeAnnotationsCucumber();
			System.out.println("nb annotation cucumber trouvé ="+listeAnnotations.size());
			
			listeAnnotations .stream() //
			.map(nom -> " - " + nom) //
			.forEach(System.out::println);

		} catch (DocletCucumberException e) {
			System.out.println(e.getMessage());
		}		
	}

	static public  boolean isNotNullAndNotEmpty(String string) {
		return string != null && !string.isEmpty();
	}

	static public  boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}

	/**
	 * Cette méthode permet de lister toutes les classes d'un package donné
	 * 
	 * @param pckgname
	 *            Le nom du package à lister
	 * @return La liste des classes
	 */
	static List<String> recupererListeAnnotationsCucumber() throws DocletCucumberException {
		List<String> classes = traitementJarParClassPath();
		
		if (classes.isEmpty()) {
System.out.println("le jar "+ PART_NOM_JAR_CUCUMBER +" n'a pas été trouvé dans le classPath");			
			classes = traitementJarParClassLoader();

			if (classes.isEmpty()) {
				throw new DocletCucumberException("le jar "+ PART_NOM_JAR_CUCUMBER +" n'a pas été trouvé dans le classLoader!");
			}		
		}
		return classes;
	}

	private static List<String> traitementJarParClassLoader() {
		List<String> classes = new ArrayList<>();
		String nomPackage = NOM_PACKAGE_CUCUMBER.replace('.', '/');

		Enumeration<URL> resource;
		try {
			resource = Util.class.getClassLoader().getResources(nomPackage);
		} catch (IOException e) {
			// TODO levé une log
			// si problème on sort
			return classes;
		}

		Collections.list(resource).forEach(url -> {//
System.out.println("url trouvé yes! = " + url.toString());
			try {
				URLConnection con = url.openConnection();
				JarFile jfile = null;

				if (con instanceof JarURLConnection) {
					// Should usually be the case for traditional JAR files.
					JarURLConnection jarCon = (JarURLConnection) con;
					jfile = jarCon.getJarFile();

					classes.addAll(traitementJar(jfile));
				}
			} catch (IOException e) {
				// TODO levé une log
				// si problème on ne fait rien et on passe au jar suivant
			}
		});
		return classes;

	}

	/**
	 * Cette méthode retourne les annotations Cucumber trouvées dans le jar
	 * passé en paramètre
	 * 
	 * @param jar
	 *            - le chemin dans le classepath system du jar
	 * @return la liste des annotations Cucumber trouvées
	 */
	private static List<String> traitementJarParClassPath() {
		List<String> classes = new ArrayList<>();

		// On récupère toutes les entrées du CLASSPATH
		String[] entries = System.getProperty("java.class.path").split(System.getProperty("path.separator"));

		// Pour toutes ces entrées, on verifie si elles contiennent un jar
		for (final String jar : entries) {
			if (jar.endsWith(".jar")) {
System.out.println("jar du classpath :" + jar);
				if (isNotNullAndNotEmpty(jar) && jar.contains(PART_NOM_JAR_CUCUMBER)) {
System.out.println("Jar "+PART_NOM_JAR_CUCUMBER+" trouvé yes!:" + jar);
					try (JarFile jfile = new JarFile(jar);) {

						classes.addAll(traitementJar(jfile));
					} catch (IOException e) {
						// TODO on leve une Log
						// Et on ne fait rien car pas le jar qui nous intéresse
						// ne devrait pas arriver
					}
				}
			}
		}
		return classes;
	}

	/**
	 * Cette méthode retourne la liste des annotations Cucumber contenu dans le
	 * jarFile et commencant par
	 * {@link fr.pylsoft.doclet.Util.NOM_PACKAGE_CUCUMBER}
	 * 
	 * @param jarFile
	 *            - le jarFile contenant le package
	 *            {@link fr.pylsoft.doclet.Util.NOM_PACKAGE_CUCUMBER}
	 * @return la liste des annotations Cucumber trouvées
	 */
	private static List<String> traitementJar(final JarFile jarFile) {

		return Collections.list(jarFile.entries()).stream() //
				.filter(element -> element.getName().matches("^"+NOM_PACKAGE_CUCUMBER.replace(".", "/")+"/(.*)/(.*).class$")) //
				.map(element -> element.getName().replace('/', '.').replaceAll(".class", "")) //
				.map(nomClasse -> { 
					try {
						return Class.forName(nomClasse).getSimpleName();
					} catch (ClassNotFoundException e) {
						// TODO on leve une log
System.out.println("élément pas une classe :" + nomClasse);
						// on ne fait rien et on passe à la classe suivante
						// ne devrait pas arriver
						return "";
					}
				}) //
				.collect(Collectors.toList());
	}

	public static List<String> extraireListePhrases(final String phrase) {
		return extraireListePhrases(phrase, new ArrayList<>());
	}

	private static List<String> extraireListePhrases(final String phrase, final List<String> listePhrases) {

		List<String> listePhrasesRetour = listePhrases;

		Matcher matcher = pattern.matcher(phrase);

		if (matcher.matches()) {
			for (int index = 1; index <= matcher.groupCount(); index++) {
				listePhrasesRetour = extraireListePhrases(matcher.group(index), listePhrasesRetour);
			}
		} else {
			listePhrasesRetour = Arrays.asList(phrase.split("\\|")).stream() //
					.map(boutPhrase -> {
						if (listePhrases.size() == 0) {
							return Arrays.asList(boutPhrase);
						} else {
							return listePhrases.stream().map(debutPhrase -> (debutPhrase + boutPhrase))
									.collect(Collectors.toList());
						}
					}).flatMap(List::stream).collect(Collectors.toList());
		}

		return listePhrasesRetour;
	}

	public static String ajoutParametreDansPhrasePossible(final String phrasePossible, final Parameter[] parametres) {

		String phraseApresTraitement = phrasePossible;
		for (Parameter parametre : parametres) {
			String nomParametre = "[" + parametre.name() + "]";
			phraseApresTraitement = phraseApresTraitement.replaceFirst("\\([^\\)]*\\)|(:$)", "$1" + nomParametre);
		}

		return phraseApresTraitement;
	}
}
