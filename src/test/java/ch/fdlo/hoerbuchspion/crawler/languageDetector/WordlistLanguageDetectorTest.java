package ch.fdlo.hoerbuchspion.crawler.languageDetector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class WordlistLanguageDetectorTest {
  @TempDir
  static Path tmpDir;
  static Path DICT_FILE_EN;
  static Path DICT_FILE_DE;
  WordlistLanguageDetector instance;

  @BeforeAll
  static void beforeAll() throws IOException {
    DICT_FILE_EN = tmpDir.resolve("en.txt");
    DICT_FILE_DE = tmpDir.resolve("de.txt");

    // As we use a set in the implementation doubles in the wordlist should not
    // matter
    Files.writeString(DICT_FILE_EN, """
        the
        die
        Die
        car
        """);
    Files.writeString(DICT_FILE_DE, """
        der
        die
        das
        Auto
        """);
  }

  @BeforeEach
  void beforeEach() throws IOException {
    this.instance = new WordlistLanguageDetector();

    this.instance.addWordlist(Language.EN, DICT_FILE_EN);
    this.instance.addWordlist(Language.DE, DICT_FILE_DE);
  }

  @Test
  void mostHitsWin() throws IOException {
    assertEquals(Language.DE, this.instance.detectLanguage("das Auto"));
  }

  @Test
  void sameHitCountGivesTie() {
    assertEquals(Language.UNKNOWN, this.instance.detectLanguage("die"));
  }

  @Test
  void noHitsMeansUnknown() {
    assertEquals(Language.UNKNOWN, this.instance.detectLanguage("Bohrmaschine"));
  }

  @Test
  void caseDoesNotMatter() {
    assertEquals(Language.DE, this.instance.detectLanguage("auto"));
  }

  @Test
  void textGetsNormalized() {
    // 1 hit per language should result in a tie
    assertEquals(Language.UNKNOWN, this.instance.detectLanguage("   car   Auto"));
  }
}
