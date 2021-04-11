package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyAlbumObject extends NamedSpotifyObject {
    public SpotifyAlbumObject(String id, String name, SpotifyObject foundVia) {
        super(id, name, foundVia);
    }

    @Override
    public String toString() {
        return "ALBUM:" + super.toString();
    }
}
