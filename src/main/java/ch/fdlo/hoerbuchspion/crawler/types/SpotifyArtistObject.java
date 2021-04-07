package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyArtistObject extends SpotifyObject {
    public SpotifyArtistObject(String id, String name) {
        super(id, name, SpotifyObjectType.ARTIST);
    }
}
