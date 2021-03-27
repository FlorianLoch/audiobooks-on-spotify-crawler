package ch.fdlo.hoerbuchspion.crawler.types;

import com.wrapper.spotify.model_objects.specification.Image;

import javax.persistence.Embeddable;

@Embeddable
public class ImageURLs {
    private String large;
    private String medium;
    private String small;

    public String getLarge() {
        return large;
    }

    public String getMedium() {
        return medium;
    }

    public String getSmall() {
        return small;
    }

    // Required by JPA
    private ImageURLs() {
    }

    private ImageURLs(String large, String medium, String small) {
        this.large = large;
        this.medium = medium;
        this.small = small;
    }

    public static ImageURLs from(Image[] albumArts) {
        // Spotify provides different resolutions for album arts and artist images.
        // They seem to have a fixed width, the height does not seem to be fixed though.
        // For albums Spotify seems to provide three resolutions:
        // - 640px
        // - 300px
        // - 64px
        // For artists there seem to be four:
        // - 1000px
        // - 640px
        // - 200px
        // - 64px (we ignore this one)
        // Though, I did not find official information on whether
        // there can be more (or also less) versions of an artist image than this. We simply assume there are three and
        // and consider the three highest resolutions.
        if (albumArts.length < 3) {
            // TODO: Log assertion error
            return new ImageURLs("", "", "");
        }

        return new ImageURLs(albumArts[0].getUrl(), albumArts[1].getUrl(), albumArts[2].getUrl());
    }
}
