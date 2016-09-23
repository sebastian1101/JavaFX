package com.capgemini.chess.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.service.StatisticsService;
import com.capgemini.chess.service.mapper.UserStatsMapper;
import com.capgemini.chess.service.to.UserStatsTO;

/**
 * Implementation of {@link StatisticsService}.
 * 
 * @author Michal Bejm
 *
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

	@Autowired
	private UserDao userDao;

	/** {@inheritDoc} */
	@Override
	public UserStatsTO getUserStats(Long id) {
		UserEntity userEntity = userDao.get(id);
		
		UserStatsTO userStatsTO = UserStatsMapper.map(userEntity);
		userStatsTO.setPosition(userDao.findPlayersPostionInRank(userEntity.getLevel(), 
				userEntity.getPoints(), userEntity.getId()));
		return userStatsTO;
	}

	/** {@inheritDoc} */
	@Override
	public List<UserStatsTO> readLeaderboard(Long id) {
		UserEntity userEntity = userDao.get(id);
		
		int position = userDao.findPlayersPostionInRank(userEntity.getLevel(), 
				userEntity.getPoints(), userEntity.getId());
		
		List<UserStatsTO> leaderboard = new ArrayList<>();
		if (position < 15) {
			// show common list of players
			List<UserEntity> topUsers = userDao.findTopUsers(0, position+9);
			int currentPosition = 1;
			for (UserEntity topUser : topUsers) {
				addToLeaderboard(leaderboard, currentPosition, topUser);
				currentPosition++;
			}
		}
		else {
			// add top 3 players
			List<UserEntity> topUsers = userDao.findTopUsers(0, 3);
			int currentPosition = 1;
			for (UserEntity topUser : topUsers) {
				addToLeaderboard(leaderboard, currentPosition, topUser);
				currentPosition++;
			}
			
			// add adjacent players (+/- 10)
			int firstPosition = position - 11;
			List<UserEntity> adjacentUsers = userDao.findTopUsers(firstPosition, 21);
			currentPosition = firstPosition + 1;
			for (UserEntity adjacentUser : adjacentUsers) {
				addToLeaderboard(leaderboard, currentPosition, adjacentUser);
				currentPosition++;
			}
		}
		
		return leaderboard;
	}

	private void addToLeaderboard(List<UserStatsTO> leaderboard, int currentPosition, UserEntity topUser) {
		UserStatsTO userStatsTO = UserStatsMapper.map(topUser);
		userStatsTO.setPosition(currentPosition);
		leaderboard.add(userStatsTO);
	}
}
