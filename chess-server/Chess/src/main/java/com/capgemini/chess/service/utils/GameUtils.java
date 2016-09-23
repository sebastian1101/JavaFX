package com.capgemini.chess.service.utils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.capgemini.chess.dataaccess.entities.ChallengeEntity;
import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.dataaccess.enumerations.GameState;

/**
 * Utility class for game operations
 * 
 * @author Michal Bejm
 *
 */
public class GameUtils {

	/**
	 * Calculates number of wins of a player with given userId from a list of {@link GameEntity}.
	 * 
	 * @param userId Id of the user.
	 * @param gameEntities game entities.
	 * @return number of wins.
	 */
	public static int calcultateWins(long userId, List<GameEntity> gameEntities) {
		int gamesWon = 0;
		
		for (GameEntity gameEntity : gameEntities) {
			if (gameEntity.getState() == GameState.WHITE_WON) {
				if (gameEntity.getChallenge().getSender().getId() ==
						userId) {
					gamesWon++;
				}
			}
			else if (gameEntity.getState() == GameState.BLACK_WON) {
				if (gameEntity.getChallenge().getReceiver().getId() ==
						userId) {
					gamesWon++;
				}
			}
		}
		
		return gamesWon;
	}
	
	/**
	 * Calculates number of loses of a player with given userId from a list of {@link GameEntity}.
	 * 
	 * @param userId Id of the user.
	 * @param gameEntities game entities.
	 * @return number of loses.
	 */
	public static int calcultateLoses(long userId, List<GameEntity> gameEntities) {
		int gamesLost = 0;
		
		for (GameEntity gameEntity : gameEntities) {
			if (gameEntity.getState() == GameState.WHITE_WON) {
				if (gameEntity.getChallenge().getReceiver().getId() ==
						userId) {
					gamesLost++;
				}
			}
			else if (gameEntity.getState() == GameState.BLACK_WON) {
				if (gameEntity.getChallenge().getSender().getId() ==
						userId) {
					gamesLost++;
				}
			}
		}
		
		return gamesLost;
	}
	
	/**
	 * Calculates number of draws of a player with given userId from a list of {@link GameEntity}.
	 * 
	 * @param userId Id of the user.
	 * @param gameEntities game entities.
	 * @return number of draws.
	 */
	public static int calcultateDraws(long userId, List<GameEntity> gameEntities) {
		int gamesDrawn = 0;
		
		for (GameEntity gameEntity : gameEntities) {
			if (gameEntity.getState() == GameState.DRAW) {
				if (gameEntity.getChallenge().getReceiver().getId() == userId 
						|| gameEntity.getChallenge().getSender().getId() == userId) {
					gamesDrawn++;
				}
			}
		}
		
		return gamesDrawn;
	}
	
	/**
	 * Extracts challenges that are in particular state.
	 * 
	 * @param sentChallenges sent challenges.
	 * @param receivedChallenges received challenges.
	 * @param gameStates states to check.
	 * @return filtered list of challenges.
	 */
	public static List<GameEntity> extractGamesByStates(List<ChallengeEntity> sentChallenges, 
			List<ChallengeEntity> receivedChallenges, EnumSet<GameState> gameStates) {
		
		List<ChallengeEntity> challenges = new ArrayList<>();
		challenges.addAll(sentChallenges);
		challenges.addAll(receivedChallenges);
		
		List<GameEntity> filteredList = new ArrayList<>();
		
		for (ChallengeEntity challengeEntity : challenges) {
			if (challengeEntity.getGame() != null &&
					gameStates.contains(challengeEntity.getGame().getState())) {
				filteredList.add(challengeEntity.getGame());
			}
		}
		
		return filteredList;
	}
}
