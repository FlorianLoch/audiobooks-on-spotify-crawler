package ch.fdlo.hoerbuchspion.crawler.types;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CRAWL_STATS_KV")
public class CrawlStatsKV {
  @Id
  @Enumerated(EnumType.STRING)
  private KVKey key;
  private String value;

  // Required by JPA
  CrawlStatsKV() {
  }

  public CrawlStatsKV(KVKey key, String value) {
    this.key = key;
    this.value = value;
  }

  public CrawlStatsKV(KVKey key, int value) {
    this(key, Integer.toString(value));
  }

  public CrawlStatsKV(KVKey key, long value) {
    this(key, Long.toString(value));
  }

  public KVKey getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    return this.key.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof CrawlStatsKV) {
      return this.key.equals(((CrawlStatsKV) obj).key);
    }

    return false;
  }

  public enum KVKey {
    PLAYLISTS_CONSIDERED_COUNT, PROFILES_CONSIDERED_COUNT, ARTISTS_CONSIDERED_COUNT, ALBUMS_FOUND_COUNT,
    ALBUM_DETAILS_FETCHED_COUNT, ARTIST_DETAILS_FETCHED_COUNT, DURATION_LAST_RUN_MS, TOTAL_API_REQUESTS_COUNT;
  }
}
