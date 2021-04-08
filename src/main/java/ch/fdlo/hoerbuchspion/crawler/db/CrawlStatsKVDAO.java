package ch.fdlo.hoerbuchspion.crawler.db;

import ch.fdlo.hoerbuchspion.crawler.types.CrawlStatsKV;

import javax.persistence.EntityManager;
import java.util.Set;

public class CrawlStatsKVDAO {
    private final EntityManager em;

    public CrawlStatsKVDAO(EntityManager em) {
        this.em = em;
    }

    public void upsert(Set<CrawlStatsKV> crawlStats) {
        DBHelper.mergeIterable(crawlStats, em);
    }

    public void truncateTable() {
        this.em.getTransaction().begin();
        this.em.createNativeQuery("DELETE FROM CRAWL_STATS_KV").executeUpdate();
        this.em.getTransaction().commit();
    }
}
