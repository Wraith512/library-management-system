package dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface enforcing the CRUD contract.
 * Demonstrates: Interface usage (Abstraction).
 *
 * @param <T>  the entity type
 * @param <ID> the primary-key type
 */
public interface GenericDAO<T, ID> {

    /**
     * Persist a new entity to the database.
     * @return true if the insert succeeded
     */
    boolean create(T entity);

    /**
     * Find an entity by its primary key.
     */
    Optional<T> findById(ID id);

    /**
     * Return all entities of this type.
     */
    List<T> findAll();

    /**
     * Persist changes to an existing entity.
     * @return true if the update affected at least one row
     */
    boolean update(T entity);

    /**
     * Remove an entity from the database by primary key.
     * @return true if the delete affected at least one row
     */
    boolean delete(ID id);

    /**
     * Search entities by a keyword string (implementation-specific columns).
     */
    List<T> search(String keyword);
}
