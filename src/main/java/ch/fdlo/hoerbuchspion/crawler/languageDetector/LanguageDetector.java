package ch.fdlo.hoerbuchspion.crawler.languageDetector;

public interface LanguageDetector {
    Language detectLanguage(String text);
}
