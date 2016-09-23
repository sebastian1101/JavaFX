package com.capgemini.chess.dataaccess.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for Data Access Object.
 * 
 * @author Michal Bejm
 *
 */
public interface Dao<T, K extends Serializable> {

    T create(T entity);

    T get(K id);

    List<T> getAll();

    T update(T entity);

    void delete(T entity);

    void delete(K id);

    void deleteAll();

    long count();

    boolean exists(K id);
}
