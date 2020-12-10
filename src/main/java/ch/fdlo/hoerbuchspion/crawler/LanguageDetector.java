package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

  static {
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

  public static Language detectLanguage(String text) {
    var result = detector.detect(tOF.forText(text));
    if (result.isPresent()) {
      return Language.fromISO_639_1(result.get().getLanguage());
    }

    return Language.UNKNOWN;
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
