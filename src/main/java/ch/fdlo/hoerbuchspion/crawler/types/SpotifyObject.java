package ch.fdlo.hoerbuchspion.crawler.types;

public abstract class SpotifyObject {
    private String id;

    protected SpotifyObject(String id) {
        this.id = id;
    }

    // Required by Jackson
    protected SpotifyObject() {
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getId();
    }
}
