package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyPlaylistObject extends NamedSpotifyObject {
    public SpotifyPlaylistObject(String id, String name) {
        super(id, name);
    }

    // Required by Jackson
    private SpotifyPlaylistObject() {}

    @Override
    public String toString() {
        return "PLAYLIST:" + super.toString();
    }
}
