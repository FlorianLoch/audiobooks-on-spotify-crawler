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
    DBHelper.mergeIterable(albums, em);
  }

  public boolean recordExists(String albumID) {
    var query = this.em.createQuery("SELECT COUNT(1) FROM Album a WHERE a.id = :id", Long.class);
    query.setParameter("id", albumID);

    return query.getSingleResult() == 1;
  }

  public void truncateTables() {
    this.em.getTransaction().begin();
    this.em.createNativeQuery("DELETE FROM ALBUM").executeUpdate();
    this.em.createNativeQuery("DELETE FROM ARTIST").executeUpdate();
    this.em.getTransaction().commit();
  }

  public Album findById(String id) {
    return this.em.find(Album.class, id);
  }
}
