package com.capgemini.chess.dataaccess.dao;

import java.util.EnumSet;
import java.util.List;

import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.Level;

/**
 * Data Access Object for retrieving User data.
 * 
 * @author Michal Bejm
 *
 */
public interface UserDao extends Dao<UserEntity, Long> {

	/**
	 * Finds a user by login.
	 * 
	 * @param login login of the user.
	 * @return user.
	 */
	public UserEntity findUsersByLogin(String login);
	
	/**
	 * Finds users by levels.
	 * 
	 * @param levels levels of the users.
	 * @param id of the user that is excluded from the search.
	 * @return list of users.
	 */
	public List<UserEntity> findUsersByLevels(EnumSet<Level> levels, long id);
	
	/**
	 * Finds top-ranked users.
	 * 
	 * @param firstResult position of first user on the list.
	 * @param maxResults number of results to search.
	 * @return list of users.
	 */
	public List<UserEntity> findTopUsers(int firstResult, int maxResults);
	
	/**
	 * Finds position of the player in rank.
	 * 
	 * @param level level of the player.
	 * @param points points of the player.
	 * @param id id of the player.
	 * @return player's posistion in rank.
	 */
	public int findPlayersPostionInRank(Level level, int points, long id);
	
	/**
	 * Finds users by given parameters.
	 * 
	 * @param login login of the user.
	 * @param name name of the user.
	 * @param surname surname of the user.
	 * @return list of users which match the given parameters.
	 */
	public List<UserEntity> findUsers(String login, String name, String surname);
}
