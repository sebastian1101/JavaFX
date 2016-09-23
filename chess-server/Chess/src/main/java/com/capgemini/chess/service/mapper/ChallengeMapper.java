package com.capgemini.chess.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.capgemini.chess.dataaccess.entities.ChallengeEntity;
import com.capgemini.chess.service.to.ChallengeTO;

/**
 * Mapper for converting {@link ChallengeEntity} and {@link ChallengeTO}
 * 
 * @author Michal Bejm
 *
 */
public class ChallengeMapper {
	
	public static ChallengeTO map(ChallengeEntity challengeEntity) {
		if (challengeEntity != null) {
			ChallengeTO challengeTO = new ChallengeTO();
			challengeTO.setId(challengeEntity.getId());
			challengeTO.setReceiver(UserProfileMapper.map(challengeEntity.getReceiver()));
			challengeTO.setReceiverLevel(challengeEntity.getReceiverLevel());
			challengeTO.setSender(UserProfileMapper.map(challengeEntity.getSender()));
			challengeTO.setSenderLevel(challengeEntity.getSenderLevel());
			return challengeTO;
		}
		return null;
	}
	
	public static List<ChallengeTO> map2TOs(List<ChallengeEntity> challengeEntities) {
		return challengeEntities.stream().map(ChallengeMapper::map).collect(Collectors.toList());
	}
}
