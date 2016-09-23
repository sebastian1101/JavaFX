package com.capgemini.chess.dataaccess.dao.impl;

import java.util.EnumSet;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.Level;

/**
 * Implementation of {@link UserDao}.
 * 
 * @author Michal Bejm
 *
 */
@Repository
public class UserDaoImpl extends AbstractDao<UserEntity, Long> implements UserDao {

	/** {@inheritDoc} */
	@Override
	public UserEntity findUsersByLogin(String login) {
		Query query = entityManager.createQuery("SELECT u FROM UserEntity u "
				+ "WHERE u.login = :login");
		query.setParameter("login", login);
		try {
			return (UserEntity) query.getSingleResult();
		}
		catch (NoResultException ex) {
			return null;
		}
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserEntity> findUsersByLevels(EnumSet<Level> levels, long id) {
		Query query = entityManager.createQuery("SELECT u FROM UserEntity u "
				+ "WHERE u.id != :id AND u.level IN :levels");
		query.setParameter("id", id);
		query.setParameter("levels", levels);
		return query.getResultList();
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserEntity> findTopUsers(int firstResult, int maxResults) {
		Query query = entityManager.createQuery("SELECT u FROM UserEntity u "
				+ "ORDER BY u.level desc, u.points desc, u.id asc")
				.setFirstResult(firstResult)
				.setMaxResults(maxResults);
		return query.getResultList();
	}

	/** {@inheritDoc} */
	@Override
	public int findPlayersPostionInRank(Level level, int points, long id) {
		Query query = entityManager.createQuery("SELECT count(u) FROM UserEntity u "
				+ "WHERE (u.level > :level) OR "
				+ "(u.level = :level AND u.points > :points) OR"
				+ "(u.level = :level AND u.points = :points AND u.id < :id)");
		query.setParameter("id", id);
		query.setParameter("level", level);
		query.setParameter("points", points);
		return (int) (long) query.getSingleResult()+1;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserEntity> findUsers(String login, String name, String surname) {
		Query query = entityManager.createQuery("SELECT u FROM UserEntity u "
				+ "WHERE u.login = :login OR u.name = :name OR u.surname = :surname");
		query.setParameter("login", login);
		query.setParameter("name", name);
		query.setParameter("surname", surname);
		return query.getResultList();
	}

}
