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
  private static List<Tuple<String>> DEBIAN_WORDLISTS = new ArrayList<>();
  private List<Tuple<Set<String>>> wordlists = new LinkedHashMap<>();

  static {
    DEBIAN_WORDLISTS.add(new Tuple(Language.EN, "/usr/share/dict/american-english-huge"));
    DEBIAN_WORDLISTS.add(new Tuple(Language.DE, "/usr/share/dict/ngerman"));
  }

  public void addWordlist(Language language, String file) throws IOException {
    Set<String> set = new HashSet<>();

    try (Stream<String> stream = Files.lines(Paths.get(file))) {
      stream.forEach(word -> {
        set.add(word.toLowerCase());
      });
    }

    wordlists.put(language, set);
  }

  private void tryToAddDebianWordlists() {

    this.addWordlist(Language.EN, ); // "insane" also contains words like
                                                                            // "Der" - not helpful.
    this.addWordlist();
    System.out.println("Loading wordlists took: " + Duration.between(start, end)); // prints PT1M3.553S
  }catch(

  IOException e)
  {
    // TODO: Auto-generated catch block
    e.printStackTrace();
  }
  }

  public Language detectLanguage(String text) {
    var mostHits = 0;
    var languageWithMostHits = Language.UNKNOWN;

    text = normalizeText(text);

    System.out.println(text);

    for (var language : wordlists.keySet()) {
      var hits = countHits(text, wordlists.get(language));

      System.out.println(language + ": " + hits);

      // FIXME: in case several languages have the same hit count we should return
      // UNKNOWN

      if (hits > mostHits) {
        mostHits = hits;
        languageWithMostHits = language;
      }
    }

    return languageWithMostHits;
  }

  private static String normalizeText(String str) {
    // TODO: Add rough explaination
    return str.strip().toLowerCase().replaceAll("\\(", "").replaceAll("\\)", "")
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

  private class Tuple<T> {
    public Language key;
    public T value;

    public Tuple(Language key, T value) {
      this.key = key;
      this.value = value;
    }
  }

}
