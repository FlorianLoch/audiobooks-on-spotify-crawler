package ch.fdlo.hoerbuchspion.crawler.languageDetector;

public class CombiningLanguageDetector implements LanguageDetector {

  private LanguageDetector[] detectors;

  // This detector gets initialized with an array of other detectors,
  // calling them in the order given until one is able to detect a language.
  public CombiningLanguageDetector(LanguageDetector... detectors) {
    this.detectors = detectors;
  }

  // Runs all detectors until one detects a language other than Language.UNKNOWN.
  // In case no detector is able to detect the language Language.UNKNOWN will be
  // returned.
  @Override
  public Language detectLanguage(String text) {
    for (var detector : this.detectors) {
      var detectedLanguage = detector.detectLanguage(text);

      if (detectedLanguage != Language.UNKNOWN) {
        return detectedLanguage;
      }
    }

    return Language.UNKNOWN;
  }

}
