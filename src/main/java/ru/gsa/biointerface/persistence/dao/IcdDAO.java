package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.IcdEntity;
import ru.gsa.biointerface.persistence.PersistenceException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdDAO extends AbstractDAO<IcdEntity, Integer> {
    protected static IcdDAO dao;

    private IcdDAO() throws PersistenceException {
        super();
    }

    public static IcdDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new IcdDAO();

        return dao;
    }
}
