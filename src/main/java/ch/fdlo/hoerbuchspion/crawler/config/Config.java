package ch.fdlo.hoerbuchspion.crawler.config;

import ch.fdlo.hoerbuchspion.crawler.types.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class Config {
    public int interval = 24 * 3600; // value given in seconds
    public List<SpotifyCategoryObject> categories = Collections.emptyList();
    public List<SpotifyProfileObject> profiles = Collections.emptyList();
    public List<SpotifyPlaylistObject> playlists = Collections.emptyList();
    public List<SpotifyArtistObject> artists = Collections.emptyList();

    public Config() {
    }

    public static Config LoadConfig(InputStream in) throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory());

        return mapper.readValue(in, Config.class);
    }
}
