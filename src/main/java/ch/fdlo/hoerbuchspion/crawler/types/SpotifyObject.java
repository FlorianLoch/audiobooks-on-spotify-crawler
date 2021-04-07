package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyObject {
    private String id;
    private String name;
    private SpotifyObjectType type;

    protected SpotifyObject(String id, String name, SpotifyObjectType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return type + ": " + name + "(" + id + ")";
    }

    protected enum SpotifyObjectType {
        ALBUM, ARTIST, CATEGORY, PLAYLIST, PROFILE;
    }
}
