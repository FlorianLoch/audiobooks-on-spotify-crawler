package ch.fdlo.hoerbuchspion.crawler.types;

public class NamedSpotifyObject extends SpotifyObject {
    private String name;

    protected NamedSpotifyObject(String id, String name, SpotifyObject foundVia) {
        super(id, foundVia);
        this.name = name;
    }

    // Required by Jackson
    protected NamedSpotifyObject() {
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName() + ", " + super.toString();
    }
}
