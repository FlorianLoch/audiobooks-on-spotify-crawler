package ch.fdlo.hoerbuchspion.crawler.languageDetector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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

public class WordlistLanguageDetector implements LanguageDetector {
  private static List<LangFileTuple> DEBIAN_WORDLISTS = new ArrayList<>();
  // A map looks suitable here too but it isn't as the order of the wordlists has to be deterministic
  private List<WordlistTuple> wordlists = new ArrayList<>();

  static {
    DEBIAN_WORDLISTS.add(new LangFileTuple(Language.EN, "/usr/share/dict/american-english-huge"));
    DEBIAN_WORDLISTS.add(new LangFileTuple(Language.DE, "/usr/share/dict/ngerman"));
  }

  public void addWordlist(Language language, String file) throws IOException {
    Set<String> set = new HashSet<>();

    try (Stream<String> stream = Files.lines(Paths.get(file))) {
      stream.forEach(word -> {
        set.add(word.toLowerCase());
      });
    }

    wordlists.add(new WordlistTuple(language, set));
  }

  public void tryToAddDebianWordlists() {
    for (var wordlistTuple : DEBIAN_WORDLISTS) {
      try {
        this.addWordlist(wordlistTuple.lang, wordlistTuple.file);
        System.out.println("Successfully loaded wordlist for '" + wordlistTuple.lang + "' from '" + wordlistTuple.file + "'.");
      } catch (IOException e) {
        System.out.println("Failed to load wordlist for '" + wordlistTuple.lang + "' from '" + wordlistTuple.file + "': " + e.getMessage());
        // TODO: maybe add how to install it?
      }
    }
  }

  public Language detectLanguage(String text) {
    var mostHits = 0;
    var languageWithMostHits = Language.UNKNOWN;

    text = normalizeText(text);

    for (var wordlistTuple : this.wordlists) {
      var hits = countHits(text, wordlistTuple.wordlist);

      // TODO: in case several languages have the same hit count we should return
      // Language.UNKNOWN

      if (hits > mostHits) {
        mostHits = hits;
        languageWithMostHits = wordlistTuple.lang;
      } else if (hits == mostHits) {
        languageWithMostHits = Language.UNKNOWN;
      }
    }

    return languageWithMostHits;
  }

  private static String normalizeText(String str) {
    // TODO: Add rough explanation on why this is done
    return str.strip().toLowerCase().replaceAll("\\(", "").replaceAll("\\)", "");
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

  private static class LangFileTuple {
    public Language lang;
    public String file;

    public LangFileTuple(Language lang, String file) {
      this.lang = lang;
      this.file = file;
    }
  }

  private static class WordlistTuple {
    public Language lang;
    public Set<String> wordlist; // Yes, it isn't an actual list...

    public WordlistTuple(Language lang, Set<String> wordlist) {
      this.lang = lang;
      this.wordlist = wordlist;
    }
  }

}
