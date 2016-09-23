package com.capgemini.chess.service.to;

import com.capgemini.chess.dataaccess.entities.ChallengeEntity;

/**
 * Transport object which represents {@link ChallengeEntity} and challenged player.
 * 
 * @author Michal Bejm
 *
 */
public class ChallengeLineTO {

	private long id;
	private UserStatsTO opponent;
	private int profit;
	private int loss;

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public UserStatsTO getOpponent() {
		return opponent;
	}
	
	public void setOpponent(UserStatsTO opponent) {
		this.opponent = opponent;
	}
	
	public int getProfit() {
		return profit;
	}
	
	public void setProfit(int profit) {
		this.profit = profit;
	}
	
	public int getLoss() {
		return loss;
	}
	
	public void setLoss(int loss) {
		this.loss = loss;
	}
}
