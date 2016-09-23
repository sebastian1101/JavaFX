package com.capgemini.chess.service;

import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.GameState;

/**
 * Service for player's rank management.
 * 
 * @author Michal Bejm
 *
 */
public interface RankService {

	/**
	 * Updates players statistics after a game based on the result.
	 * 
	 * @param gameEntity game to be updated.
	 * @param gameState result of the game.
	 */
	public void updatePlayersStats(GameEntity gameEntity, GameState gameState);
	
	/**
	 * Calculate expected profit if the user wins with the opponent.
	 * 
	 * @param user user for who the profit is calculated.
	 * @param opponent opponent.
	 * @return expected profit.
	 */
	public int calculatePlayersProfit(UserEntity user, UserEntity opponent);
	
	/**
	 * Calculate expected loss if the user loses with the opponent.
	 * 
	 * @param user user for who the loss is calculated.
	 * @param opponent opponent.
	 * @return expected loss.
	 */
	public int calculatePlayersLoss(UserEntity user, UserEntity opponent);
}
