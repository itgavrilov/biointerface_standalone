package ru.gsa.biointerface.repository.impl;

import org.hibernate.Session;
import ru.gsa.biointerface.repository.exception.ReadException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
public abstract class AbstractRepository<Entity, Key> extends AbstractDAO<Entity, Key> {
    protected AbstractRepository() throws Exception {
        super();
    }

    public List<Entity> findAll() throws Exception {
        try (final Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Entity> cq = cb.createQuery(genericType);
            cq.from(genericType);
            List<Entity> entities = session.createQuery(cq).getResultList();
            session.getTransaction().commit();
            LOGGER.info("Reading all entities is successful");

            return entities;
        } catch (Exception e) {
            LOGGER.error("Error reading all entities", e);
            throw new ReadException(e);
        }
    }

    public boolean existsById(Key key) throws Exception {
        return findById(key).isPresent();
    }
}
