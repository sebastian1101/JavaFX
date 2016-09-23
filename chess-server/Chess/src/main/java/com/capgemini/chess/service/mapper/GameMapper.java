package com.capgemini.chess.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.service.to.GameTO;

/**
 * Mapper for converting {@link GameEntity} and {@link GameTO}.
 * 
 * @author Michal Bejm
 *
 */
public class GameMapper {
	
	public static GameTO map(GameEntity gameEntity) {
		if (gameEntity != null) {
			GameTO gameTO = new GameTO();
			gameTO.setChallenge(ChallengeMapper.map(gameEntity.getChallenge()));
			gameTO.setId(gameEntity.getId());
			gameTO.setMoves(MoveMapper.map2TOs(gameEntity.getMoves()));
			gameTO.setState(gameEntity.getState());
			gameTO.setStartDate(gameEntity.getStartDate());
			gameTO.setLastUpdateDate(gameEntity.getLastUpdateDate());
			return gameTO;
		}
		return null;
	}
	
	public static List<GameTO> map2TOs(List<GameEntity> gameEntities) {
		return gameEntities.stream().map(GameMapper::map).collect(Collectors.toList());
	}
}
