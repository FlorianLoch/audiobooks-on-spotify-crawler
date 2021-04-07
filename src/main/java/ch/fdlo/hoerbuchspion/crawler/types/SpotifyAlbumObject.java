package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyAlbumObject extends NamedSpotifyObject {
    public SpotifyAlbumObject(String id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return "ALBUM:" + super.toString();
    }
}
