package ch.fdlo.hoerbuchspion.crawler.languageDetector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CombiningLanguageDetectorTest {

  @Test
  void firstDetectorIsAbleToDetect() {
    CombiningLanguageDetector instance = new CombiningLanguageDetector(new LanguageDetector() {
      @Override
      public Language detectLanguage(String text) {
        return Language.EN;
      }
    }, new LanguageDetector() {
      @Override
      public Language detectLanguage(String text) {
        return Language.DE;
      }
    });

    assertEquals(Language.EN, instance.detectLanguage("test"));
  }

  @Test
  void firstDetectorIsNotAbleToDetect() {
    CombiningLanguageDetector instance = new CombiningLanguageDetector(new LanguageDetector() {
      @Override
      public Language detectLanguage(String text) {
        return Language.UNKNOWN;
      }
    }, new LanguageDetector() {
      @Override
      public Language detectLanguage(String text) {
        if (text == "test") {
          return Language.DE;
        }

        return Language.UNKNOWN;
      }
    });

    assertEquals(Language.DE, instance.detectLanguage("test"));
  }

}
