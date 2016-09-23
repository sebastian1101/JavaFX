package com.capgemini.chess.service.mapper;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.capgemini.chess.dataaccess.entities.ChallengeEntity;
import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.GameState;
import com.capgemini.chess.service.to.UserStatsTO;
import com.capgemini.chess.service.utils.GameUtils;

/**
 * Mapper for converting {@link UserEntity} and {@link UserStatsTO}.
 * 
 * @author Michal Bejm
 *
 */
public class UserStatsMapper {

	public static UserStatsTO map(UserEntity userEntity) {
		if (userEntity != null) {
			UserStatsTO userStatsTO = new UserStatsTO();
			userStatsTO.setId(userEntity.getId());
			userStatsTO.setLevel(userEntity.getLevel());
			userStatsTO.setLogin(userEntity.getLogin());
			userStatsTO.setName(userEntity.getName());
			userStatsTO.setPoints(userEntity.getPoints());
			userStatsTO.setSurname(userEntity.getSurname());
			setGameStats(userStatsTO, userEntity.getSentChallenges(), userEntity.getReceivedChallenges());
			return userStatsTO;
		}
		return null;
	}

	private static void setGameStats(UserStatsTO userStatsTO, List<ChallengeEntity> sentChallenges,
			List<ChallengeEntity> receivedChallenges) {
		List<GameEntity> filteredGameEntities = GameUtils.extractGamesByStates(sentChallenges,
				receivedChallenges, EnumSet.of(GameState.WHITE_WON, GameState.BLACK_WON, GameState.DRAW));
		userStatsTO.setGamesDrawn(GameUtils.calcultateDraws(userStatsTO.getId(), filteredGameEntities));
		userStatsTO.setGamesLost(GameUtils.calcultateLoses(userStatsTO.getId(), filteredGameEntities));
		userStatsTO.setGamesWon(GameUtils.calcultateWins(userStatsTO.getId(), filteredGameEntities));
		userStatsTO.setGamesPlayed(filteredGameEntities.size());
	}
	
	public static List<UserStatsTO> map2TOs(List<UserEntity> userEntities) {
		return userEntities.stream().map(UserStatsMapper::map).collect(Collectors.toList());
	}
}
