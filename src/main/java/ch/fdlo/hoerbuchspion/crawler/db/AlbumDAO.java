package ch.fdlo.hoerbuchspion.crawler.db;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import ch.fdlo.hoerbuchspion.crawler.types.Album;

public class AlbumDAO {
  public static String DEFAULT_PERSISTENCE_UNIT_NAME = "audiobooks";

  private EntityManager em;

  public AlbumDAO() {
    this(DEFAULT_PERSISTENCE_UNIT_NAME);
  }

  public AlbumDAO(String persistenceUnit) {
    var factory = Persistence.createEntityManagerFactory(persistenceUnit);

    this.em = factory.createEntityManager();
  }

  public void persist(Collection<Album> albums) {
    this.em.getTransaction().begin();

    for (Album album : albums) {
      this.em.merge(album);
    }

    this.em.getTransaction().commit();
  }
}
