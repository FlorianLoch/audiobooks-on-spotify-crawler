package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyProfileObject extends SpotifyObject {
    public SpotifyProfileObject(String id, String name) {
        super(id, name, SpotifyObjectType.PROFILE);
    }
}
