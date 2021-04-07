package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyPlaylistObject extends SpotifyObject {
    public SpotifyPlaylistObject(String id, String name) {
        super(id, name, SpotifyObjectType.PLAYLIST);
    }
}
