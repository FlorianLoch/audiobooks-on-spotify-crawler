package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObjectFactory;

public class LanguageDetector {
  private static TextObjectFactory tOF;
  private static com.optimaize.langdetect.LanguageDetector detector;
  private static Map<Language, Set<String>> wordlists = new LinkedHashMap<>();

  static {
    initializeOptimaize();
    initializeWordlists();
  }

  private static void initializeOptimaize() {
    List<LanguageProfile> languageProfiles;
    try {
      var profilesToLoad = Arrays.asList(LdLocale.fromString("de"), LdLocale.fromString("en"),
          LdLocale.fromString("fr"), LdLocale.fromString("es"), LdLocale.fromString("it"));
      languageProfiles = new LanguageProfileReader().readBuiltIn(profilesToLoad);

      // build language detector:
      detector = LanguageDetectorBuilder.create(NgramExtractors.standard()).withProfiles(languageProfiles).build();

      // create a text object factory
      tOF = CommonTextObjectFactories.forDetectingShortCleanText();
    } catch (IOException e) {
      // TODO: Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static void initializeWordlists() {
    try {
      Instant start = Instant.now();

      initializeWordlist(Language.EN, "/usr/share/dict/american-english-huge"); // "insane" also contains words like "Der" - not helpful.
      initializeWordlist(Language.DE, "/usr/share/dict/ngerman");

      Instant end = Instant.now();
      System.out.println("Loading wordlists took: " + Duration.between(start, end)); // prints PT1M3.553S
    } catch (IOException e) {
      // TODO: Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static void initializeWordlist(Language language, String file) throws IOException {
    Set<String> set = new HashSet<>();

    try (Stream<String> stream = Files.lines(Paths.get(file))) {
      stream.forEach(word -> {
        set.add(word.toLowerCase());
      });
    }

    wordlists.put(language, set);
  }

  public static Language detectUsingOptimaize(String text) {
    var result = detector.detect(tOF.forText(text));
    if (result.isPresent()) {
      return Language.fromISO_639_1(result.get().getLanguage());
    }

    return Language.UNKNOWN;
  }

  public static Language detectUsingWordlists(String text) {
    var mostHits = 0;
    var languageWithMostHits = Language.UNKNOWN;

    text = text.strip().toLowerCase().replaceAll("\\(", "").replaceAll("\\)", "");

    System.out.println(text);

    for (var language : wordlists.keySet()) {
      var hits = countHits(text, wordlists.get(language));

      System.out.println(language + ": " + hits);

      // FIXME: in case several languages have the same hit count we should return UNKNOWN

      if (hits > mostHits) {
        mostHits = hits;
        languageWithMostHits = language;
      }
    }

    return languageWithMostHits;
  }

  private static int countHits(String text, Set<String> wordlist) {
    int counter = 0;

    for (var word : text.split(" ")) {
      if (wordlist.contains(word)) {
        counter++;
      }
    }

    return counter;
  }

  public static Language detectLanguage(String text) {
    var detectedLanguage = detectUsingOptimaize(text);

    if (detectedLanguage == Language.UNKNOWN) {
      return detectUsingWordlists(text);
    }

    return detectedLanguage;
  }

  public enum Language {
    DE, EN, FR, ES, IT, UNKNOWN;

    public static Language fromISO_639_1(String lang) {
      switch (lang) {
        case "de":
          return DE;
        case "en":
          return EN;
        case "fr":
          return FR;
        case "es":
          return ES;
        case "it":
          return IT;
        default:
          return UNKNOWN;
      }
    }
  }
}
