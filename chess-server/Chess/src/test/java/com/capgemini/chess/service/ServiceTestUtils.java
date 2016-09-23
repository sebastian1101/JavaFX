package com.capgemini.chess.service;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.chess.dataaccess.entities.ChallengeEntity;
import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.GameState;

public class ServiceTestUtils {

	public static List<ChallengeEntity> createListOfGames(UserEntity player, int drawn, int won, int lost) {
		UserEntity opponent = new UserEntity();
		opponent.setId(player.getId()+1);
		List<ChallengeEntity> challenges = new ArrayList<>();
		for (int i = 0; i < drawn; i++) {
			ChallengeEntity challengeEntity = new ChallengeEntity();
			challengeEntity.setSender(player);
			challengeEntity.setReceiver(opponent);
			GameEntity gameEntity = new GameEntity();
			gameEntity.setState(GameState.DRAW);
			gameEntity.setChallenge(challengeEntity);
			challengeEntity.setGame(gameEntity);
			challenges.add(challengeEntity);
		}
		for (int i = 0; i < won; i++) {
			ChallengeEntity challengeEntity = new ChallengeEntity();
			challengeEntity.setSender(player);
			challengeEntity.setReceiver(opponent);
			GameEntity gameEntity = new GameEntity();
			gameEntity.setState(GameState.WHITE_WON);
			gameEntity.setChallenge(challengeEntity);
			challengeEntity.setGame(gameEntity);
			challenges.add(challengeEntity);
		}
		for (int i = 0; i < lost; i++) {
			ChallengeEntity challengeEntity = new ChallengeEntity();
			challengeEntity.setSender(player);
			challengeEntity.setReceiver(opponent);
			GameEntity gameEntity = new GameEntity();
			gameEntity.setState(GameState.BLACK_WON);
			gameEntity.setChallenge(challengeEntity);
			challengeEntity.setGame(gameEntity);
			challenges.add(challengeEntity);
		}
		return challenges;
	}
}
