package fr.pylsoft.doclet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sun.javadoc.Parameter;

public class Util {

	final static Pattern pattern = Pattern.compile("^(.*)\\(\\?\\:([^\\)]*)\\)(.*)$");

	public static void main(String[] args) {
		System.out.println("----------");
		System.out.println("- Test 1 -");
		System.out.println("----------");
//		String phrase = "le contexte (?:de|du|des|d'|le|la|les|l') (.*) de ta (?:mere|pere) (.*):";
		String phrase = "un bénéficiaire ouvrant-droit avec le matricule '(.*)' et le numéro de famille '(.*)'";
		System.out.println(phrase);
		System.out.println("-------");
		extraireListePhrases(phrase).forEach(System.out::println);
		
//		System.out.println("----------");
//		System.out.println("- Test 2 -");
//		System.out.println("----------");
//		phrase = "le contexte de (.*) de ta mere (.*):";
//		System.out.println("resultat = "+ajoutParametreDansPhrasePossible(phrase, Arrays.asList("[T1]","[T2]","[T3]")));
	}

	public static boolean isNotNullAndNotEmpty(String string) {
		return string != null && !string.isEmpty();
	}

	public static boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}

	public static List<String> extraireListePhrases(String phrase) {
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
	
	public static String ajoutParametreDansPhrasePossible(String phrasePossible, Parameter[] parametres) {
		
		String phraseApresTraitement = phrasePossible;
		for (Parameter parametre : parametres) {
			String nomParametre = "[" + parametre.name() + "]";
			phraseApresTraitement = phraseApresTraitement.replaceFirst("\\([^\\)]*\\)|(:)", "$1"+nomParametre);
		}
		
		return phraseApresTraitement;
	}	
}
