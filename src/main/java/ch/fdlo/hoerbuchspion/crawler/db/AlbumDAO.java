package ch.fdlo.hoerbuchspion.crawler.db;

import java.util.Collection;
import java.util.LinkedHashMap;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import ch.fdlo.hoerbuchspion.crawler.types.Album;

public class AlbumDAO {
  public static String DEFAULT_PERSISTENCE_UNIT_NAME = "audiobooks";

  private EntityManager em;

  public AlbumDAO(boolean verboseLogging) {
    this(DEFAULT_PERSISTENCE_UNIT_NAME, verboseLogging);
  }

  public AlbumDAO(String persistenceUnit, boolean verboseLogging) {
    var props = new LinkedHashMap<>();
    if (verboseLogging) {
      props.put("eclipselink.logging.level", "FINE");
    }

    var factory = Persistence.createEntityManagerFactory(persistenceUnit, props);

    this.em = factory.createEntityManager();
  }

  public void upsert(Collection<Album> albums) {
    this.em.getTransaction().begin();

    for (Album album : albums) {
      this.em.merge(album);
    }

    this.em.getTransaction().commit();
  }

  public boolean recordExists(Album album) {
    var query = this.em.createQuery("SELECT COUNT(1) FROM Album a WHERE a.id = :id", Long.class);
    query.setParameter("id", album.getId());

    return query.getSingleResult() == 1;
  }

  public Album findById(String id) {
    return this.em.find(Album.class, id);
  }
}
