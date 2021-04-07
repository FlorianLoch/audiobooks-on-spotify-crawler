package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyCategoryObject extends SpotifyObject {
    public SpotifyCategoryObject(String id, String name) {
        super(id, name, SpotifyObjectType.CATEGORY);
    }
}
