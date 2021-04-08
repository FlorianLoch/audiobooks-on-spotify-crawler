package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyCategoryObject extends SpotifyObject {
    public SpotifyCategoryObject(String id) {
        super(id);
    }

    // Required by Jackson
    private SpotifyCategoryObject() {
    }

    @Override
    public String toString() {
        return "CATEGORY:" + super.toString();
    }
}
