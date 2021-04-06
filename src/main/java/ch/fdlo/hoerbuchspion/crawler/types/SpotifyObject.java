package ch.fdlo.hoerbuchspion.crawler.types;

public class SpotifyObject {
    private String id;
    private String name;
    private SpotifyObjectType type;

    public static SpotifyObject artist(String id, String name) {
        return new SpotifyObject(id, name, SpotifyObjectType.ARTIST);
    }

    public static SpotifyObject album(String id, String name) {
        return new SpotifyObject(id, name, SpotifyObjectType.ALBUM);
    }

    public static SpotifyObject category(String id, String name) {
        return new SpotifyObject(id, name, SpotifyObjectType.CATEGORY);
    }

    public static SpotifyObject playlist(String id, String name) {
        return new SpotifyObject(id, name, SpotifyObjectType.PLAYLIST);
    }

    public static SpotifyObject profile(String id, String name) {
        return new SpotifyObject(id, name, SpotifyObjectType.PROFILE);
    }

    public SpotifyObject(String id, String name, SpotifyObjectType type) {
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

    public SpotifyObjectType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + ": " + name + "(" + id + ")";
    }

    enum SpotifyObjectType {
        ALBUM, ARTIST, CATEGORY, PLAYLIST, PROFILE;
    }
}
