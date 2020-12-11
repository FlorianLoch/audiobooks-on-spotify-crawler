package ch.fdlo.hoerbuchspion.crawler.languageDetector;

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
