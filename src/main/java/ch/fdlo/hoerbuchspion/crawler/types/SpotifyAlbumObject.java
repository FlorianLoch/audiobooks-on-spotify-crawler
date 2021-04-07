package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyAlbumObject extends SpotifyObject {
    public SpotifyAlbumObject(String id, String name) {
        super(id, name, SpotifyObjectType.ALBUM);
    }
}
