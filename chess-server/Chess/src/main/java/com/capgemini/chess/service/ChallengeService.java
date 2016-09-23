package com.capgemini.chess.service;

import java.util.List;

import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.exceptions.InvalidUserException;
import com.capgemini.chess.service.to.ChallengeLineTO;
import com.capgemini.chess.service.to.PendingChallengesTO;
import com.capgemini.chess.service.to.UserStatsTO;

/**
 * Service for challenge management.
 * 
 * @author Michal Bejm
 *
 */
public interface ChallengeService {
	
	/**
	 * Finds challenge suggestions for the user.
	 * 
	 * @param userId id of the user.
	 * @return challenge suggestions.
	 */
	public List<ChallengeLineTO> getChallengeSuggestions(Long userId);
	
	/**
	 * Finds pending challenges of the user.
	 * 
	 * @param userId id of the user.
	 * @return pending challengess.
	 */
	public PendingChallengesTO getPendingChallenges(Long userId);
	
	/**
	 * Creates a challenge.
	 * 
	 * @param receiverId id of receiver.
	 * @param userId id of sender.
	 * @return created challenge.
	 * @throws EntityNotFoundException in case either sender or receiver cannot be found.
	 */
	public ChallengeLineTO createChallenge(Long receiverId, Long userId) 
			throws EntityNotFoundException;
	
	/**
	 * Accepts a challenge with a given id.
	 * 
	 * @param id id of the challenge.
	 * @param userId id of the accepting user.
	 * @throws EntityNotFoundException in case challenge cannot be found.
	 * @throws InvalidUserException in case user is not allowed to accept the challenge.
	 */
	public void acceptChallenge(Long id, Long userId) 
			throws EntityNotFoundException, InvalidUserException;
	
	/**
	 * Declines a challenge with a given id.
	 * 
	 * @param id id of the challenge.
	 * @param userId id of the declining user.
	 * @throws EntityNotFoundException in case challenge cannot be found.
	 * @throws InvalidUserException in case user is not allowed to decline the challenge.
	 */
	public void declineChallenge(Long id, Long userId)
			throws EntityNotFoundException, InvalidUserException;
	
	/**
	 * Find a challenged player by login.
	 * 
	 * @param login login of the user.
	 * @return challenged player.
	 * @throws EntityNotFoundException in case user cannot be found.
	 */
	public UserStatsTO findChallengedPlayer(String login) 
			throws EntityNotFoundException;
}
