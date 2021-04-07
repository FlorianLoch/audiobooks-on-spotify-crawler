package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyArtistObject extends NamedSpotifyObject {
    public SpotifyArtistObject(String id, String name) {
        super(id, name);
    }

    // Required by Jackson
    private SpotifyArtistObject() {}

    @Override
    public String toString() {
        return "ARTIST:" + super.toString();
    }
}
