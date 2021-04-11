package ch.fdlo.hoerbuchspion.crawler.types;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class SpotifyObject {
    private String id;
    @JsonIgnore
    private SpotifyObject foundVia;

    protected SpotifyObject(String id, SpotifyObject foundVia) {
        this.id = id;
        this.foundVia = foundVia;
    }

    // Required by Jackson
    protected SpotifyObject() {
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        if (foundVia == null) {
            return getId() + " <- ENTRYPOINT";
        } else {
            return getId() + " <- " + foundVia.toString();
        }
    }
}
