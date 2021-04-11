package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyProfileObject extends SpotifyObject {
    public SpotifyProfileObject(String id) {
        super(id, null);
    }

    // Required by Jackson
    private SpotifyProfileObject() {
    }

    @Override
    public String toString() {
        return "PROFILE:" + super.toString();
    }
}
