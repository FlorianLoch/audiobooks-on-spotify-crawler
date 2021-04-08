package ch.fdlo.hoerbuchspion.crawler.languageDetector;

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

public class OptimaizeLanguageDetector implements LanguageDetector {
    private TextObjectFactory tOF;
    private com.optimaize.langdetect.LanguageDetector detector;

    public OptimaizeLanguageDetector() throws IOException {
        this.initializeOptimaize();
    }

    private void initializeOptimaize() throws IOException {
        List<LanguageProfile> languageProfiles;

        var profilesToLoad = Arrays.asList(LdLocale.fromString("de"), LdLocale.fromString("en"), LdLocale.fromString("fr"),
                LdLocale.fromString("es"), LdLocale.fromString("it"));
        languageProfiles = new LanguageProfileReader().readBuiltIn(profilesToLoad);

        this.detector = LanguageDetectorBuilder.create(NgramExtractors.standard()).withProfiles(languageProfiles).build();

        this.tOF = CommonTextObjectFactories.forDetectingShortCleanText();
    }

    public synchronized Language detectLanguage(String text) {
        var result = this.detector.detect(this.tOF.forText(text));
        if (result.isPresent()) {
            return Language.fromISO_639_1(result.get().getLanguage());
        }

        return Language.UNKNOWN;
    }

}
