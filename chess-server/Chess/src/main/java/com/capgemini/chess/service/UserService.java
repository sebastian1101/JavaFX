package com.capgemini.chess.service;

import java.util.List;

import com.capgemini.chess.service.exceptions.EntityExistsException;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.to.UserProfileTO;

/**
 * Service for user profile management.
 * 
 * @author Michal Bejm
 *
 */
public interface UserService {
	
	/**
	 * Updates a user profile.
	 * 
	 * @param userTo user profile to update.
	 * @return updated user profile.
	 */
	public UserProfileTO updateUser(UserProfileTO userTo);
	
	/**
	 * Creates a user profile.
	 * 
	 * @param userTo user profile to create.
	 * @return created user profile.
	 * @throws EntityExistsException in case a user with given login already exists.
	 */
	public UserProfileTO createUser(UserProfileTO userTo) throws EntityExistsException;
	
	/**
	 * Deletes the user with the given id.
	 * 
	 * @param id id of the user to delete.
	 */
	public void deleteUser(Long id);
	
	/**
	 * Find user profile by login.
	 * 
	 * @param login login of the user.
	 * @return user profile.
	 * @throws EntityNotFoundException in case a user with given login cannot be found.
	 */
	public UserProfileTO findUserProfileByLogin(String login) throws EntityNotFoundException;
	
	/**
	 * Finds users by given parameters.
	 * 
	 * @param login login of the user.
	 * @param name name of the user.
	 * @param surname surname of the user.
	 * @return list of users which match the given parameters.
	 */
	public List<UserProfileTO> findUsers(String login, String name, String surname);

}
