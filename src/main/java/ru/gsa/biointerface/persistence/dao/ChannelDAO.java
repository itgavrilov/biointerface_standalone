package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.persistence.DAOException;

import java.sql.*;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelDAO extends AbstractDAO<ChannelEntity> {
    protected static ChannelDAO dao;

    private ChannelDAO() throws DAOException {
        super();
    }

    public static ChannelDAO getInstance() throws DAOException {
        if (dao == null)
            dao = new ChannelDAO();

        return dao;
    }

    @Override
    public ChannelEntity insert(ChannelEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("entity is null");

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.INSERT.QUERY)) {
            statement.setString(1, entity.getName());

            if (entity.getName() != null)
                statement.setString(2, entity.getComment());
            else
                statement.setNull(2, Types.NULL);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    entity.setId(resultSet.getInt("id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("resultSet error", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return entity;
    }

    @Override
    public ChannelEntity getById(int id) throws DAOException {
        ChannelEntity entity = null;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.SELECT.QUERY)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    entity = new ChannelEntity(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("comment")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DAOException("resultSet error", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return entity;
    }

    @Override
    public boolean update(ChannelEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("entity is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.UPDATE.QUERY)) {
            statement.setInt(1, entity.getId());
            statement.setString(2, entity.getName());

            if (entity.getName() != null)
                statement.setString(3, entity.getComment());
            else
                statement.setNull(3, Types.NULL);

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public boolean delete(ChannelEntity entity) throws DAOException {
        if (entity == null)
            throw new NullPointerException("entity is null");

        boolean result;

        try (PreparedStatement statement = db.getConnection().prepareStatement(SQL.DELETE.QUERY)) {
            statement.setInt(1, entity.getId());

            result = statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return result;
    }

    @Override
    public Set<ChannelEntity> getAll() throws DAOException {
        Set<ChannelEntity> entities = new TreeSet<>();

        try (Statement statement = db.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(SQL.SELECT_ALL.QUERY)) {
            while (resultSet.next()) {
                ChannelEntity entity = new ChannelEntity(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("comment")
                );
                entities.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException("statement error", e);
        }

        return entities;
    }


    private enum SQL {
        INSERT("INSERT INTO Channel (name, comment) " +
                "VALUES ((?), (?))" +
                "RETURNING id;"),
        SELECT("SELECT * FROM Channel WHERE id = (?);"),
        UPDATE("UPDATE Channel SET name = (?), comment = (?) WHERE id = (?);"),
        DELETE("DELETE FROM Channel WHERE id = (?);"),
        SELECT_ALL("SELECT * FROM Channel;");

        final String QUERY;

        SQL(String query) {
            this.QUERY = query;
        }
    }
}