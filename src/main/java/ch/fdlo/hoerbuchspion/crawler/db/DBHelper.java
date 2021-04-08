package ch.fdlo.hoerbuchspion.crawler.db;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.LinkedHashMap;
import java.util.logging.Level;

public class DBHelper {
    public static String DEFAULT_PERSISTENCE_UNIT_NAME = "audiobooks";
    private static EntityManager instance = null;

    public static EntityManager getEntityManagerInstance(boolean verboseLogging) {
        if (instance == null) {

            var props = new LinkedHashMap<>();
            if (verboseLogging) {
                props.put(PersistenceUnitProperties.LOGGING_LEVEL, Level.FINE);
            }

            var factory = Persistence.createEntityManagerFactory(DEFAULT_PERSISTENCE_UNIT_NAME, props);

            instance = factory.createEntityManager();
        }

        return instance;
    }

    public static <T> void mergeIterable(Iterable<T> iterable, EntityManager em) {
        em.getTransaction().begin();

        for (var item : iterable) {
            em.merge(item);
        }

        em.getTransaction().commit();
    }
}
