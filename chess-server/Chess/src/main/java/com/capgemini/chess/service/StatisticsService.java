package com.capgemini.chess.service;

import java.util.List;

import com.capgemini.chess.service.to.UserStatsTO;

/**
 * Service for statistics management.
 * 
 * @author Michal Bejm
 *
 */
public interface StatisticsService {

	/**
	 * Gets the statistics of the user.
	 * 
	 * @param id id of the user.
	 * @return statistics of the user.
	 */
	public UserStatsTO getUserStats(Long id);
	
	/**
	 * Reads the leader board for the user with given id.
	 * 
	 * @param id id of the user.
	 * @return leader board.
	 */
	public List<UserStatsTO> readLeaderboard(Long id);
}
