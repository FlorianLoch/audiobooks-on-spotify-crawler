package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyArtistObject extends NamedSpotifyObject {
    public SpotifyArtistObject(String id, String name, SpotifyObject foundVia) {
        super(id, name, foundVia);
    }

    // Required by Jackson
    private SpotifyArtistObject() {
    }

    @Override
    public String toString() {
        return "ARTIST:" + super.toString();
    }
}
