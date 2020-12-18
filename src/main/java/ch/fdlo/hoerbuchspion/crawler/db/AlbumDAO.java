package ch.fdlo.hoerbuchspion.crawler.db;

import java.util.Collection;

import javax.persistence.EntityManager;

import ch.fdlo.hoerbuchspion.crawler.types.Album;

public class AlbumDAO {
  private EntityManager em;

  public AlbumDAO(EntityManager em) {
    this.em = em;
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
