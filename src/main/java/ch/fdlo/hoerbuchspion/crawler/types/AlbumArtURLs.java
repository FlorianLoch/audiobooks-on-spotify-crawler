package ch.fdlo.hoerbuchspion.crawler.types;

import com.wrapper.spotify.model_objects.specification.Image;

import javax.persistence.Embeddable;

@Embeddable
public class AlbumArtURLs {
    private String largeAlbumArtURL;
    private String mediumAlbumArtURL;
    private String smallAlbumArtURL;

    public String getLargeAlbumArtURL() {
        return largeAlbumArtURL;
    }

    public String getMediumAlbumArtURL() {
        return mediumAlbumArtURL;
    }

    public String getSmallAlbumArtURL() {
        return smallAlbumArtURL;
    }

    // Required by JPA
    private AlbumArtURLs() {
    }

    private AlbumArtURLs(String large, String medium, String small) {
        this.largeAlbumArtURL = large;
        this.mediumAlbumArtURL = medium;
        this.smallAlbumArtURL = small;
    }

    public static AlbumArtURLs from(Image[] albumArts) {
        if (albumArts.length != 3) {
            // TODO: Log assertion error
            return new AlbumArtURLs("", "", "");
        }

        return new AlbumArtURLs(albumArts[0].getUrl(), albumArts[1].getUrl(), albumArts[2].getUrl());
    }
}
