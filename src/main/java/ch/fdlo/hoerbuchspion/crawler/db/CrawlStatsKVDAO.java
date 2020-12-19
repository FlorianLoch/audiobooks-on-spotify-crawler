package ch.fdlo.hoerbuchspion.crawler.db;

import java.util.Set;

import javax.persistence.EntityManager;

import ch.fdlo.hoerbuchspion.crawler.types.CrawlStatsKV;

public class CrawlStatsKVDAO {
  private EntityManager em;

  public CrawlStatsKVDAO(EntityManager em) {
    this.em = em;
  }

  public void upsert(Set<CrawlStatsKV> crawlStats) {
    DBHelper.mergeIterable(crawlStats, em);
  }
}
