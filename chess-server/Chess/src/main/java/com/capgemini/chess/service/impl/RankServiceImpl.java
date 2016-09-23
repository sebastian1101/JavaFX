package com.capgemini.chess.service.impl;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.GameState;
import com.capgemini.chess.dataaccess.enumerations.Level;
import com.capgemini.chess.service.RankService;
import com.capgemini.chess.service.utils.GameUtils;

/**
 * Implementation of {@link RankService}.
 * 
 * @author Michal Bejm
 *
 */
@Service
public class RankServiceImpl implements RankService {
	
	@Autowired
	private UserDao userDao;

	private static final Map<Integer, Integer> WIN_BASES;
    static {
    	WIN_BASES = new HashMap<>();
    	WIN_BASES.put(-9, 1);
    	WIN_BASES.put(-8, 1);
    	WIN_BASES.put(-7, 1);
    	WIN_BASES.put(-6, 1);
    	WIN_BASES.put(-5, 1);
    	WIN_BASES.put(-4, 2);
    	WIN_BASES.put(-3, 5);
    	WIN_BASES.put(-2, 10);
    	WIN_BASES.put(-1, 20);
    	WIN_BASES.put(0, 40);
    	WIN_BASES.put(1, 80);
    	WIN_BASES.put(2, 160);
    	WIN_BASES.put(3, 320);
    	WIN_BASES.put(4, 640);
    	WIN_BASES.put(5, 1280);
    	WIN_BASES.put(6, 2560);
    	WIN_BASES.put(7, 5120);
    	WIN_BASES.put(8, 10240);
    	WIN_BASES.put(9, 20480);
    }
    
    private static final Map<Integer, Integer> LOSS_BASES;
    static {
    	LOSS_BASES = new HashMap<>();
    	LOSS_BASES.put(-9, 1);
    	LOSS_BASES.put(-8, 1);
    	LOSS_BASES.put(-7, 1);
    	LOSS_BASES.put(-6, 1);
    	LOSS_BASES.put(-5, 1);
    	LOSS_BASES.put(-4, 1);
    	LOSS_BASES.put(-3, 3);
    	LOSS_BASES.put(-2, 7);
    	LOSS_BASES.put(-1, 15);
    	LOSS_BASES.put(0, 30);
    	LOSS_BASES.put(1, 60);
    	LOSS_BASES.put(2, 120);
    	LOSS_BASES.put(3, 240);
    	LOSS_BASES.put(4, 480);
    	LOSS_BASES.put(5, 960);
    	LOSS_BASES.put(6, 1920);
    	LOSS_BASES.put(7, 3840);
    	LOSS_BASES.put(8, 7680);
    	LOSS_BASES.put(9, 15360);
    }

    /** {@inheritDoc} */
	@Override
	public void updatePlayersStats(GameEntity gameEntity, GameState gameState) {
		
		int senderProfit = 0;
		int receiverProfit = 0;
		
		if (gameState == GameState.WHITE_WON) {
			senderProfit = calculateProfit(gameEntity.getChallenge().getSender(), 
					gameEntity.getChallenge().getReceiver(), true);
			receiverProfit = calculateProfit(gameEntity.getChallenge().getReceiver(), 
					gameEntity.getChallenge().getSender(), false);
		}
		else if (gameState == GameState.BLACK_WON) {
			senderProfit = calculateProfit(gameEntity.getChallenge().getSender(), 
					gameEntity.getChallenge().getReceiver(), false);
			receiverProfit = calculateProfit(gameEntity.getChallenge().getReceiver(), 
					gameEntity.getChallenge().getSender(), true);
		}
		
		updatePlayer(gameEntity.getChallenge().getSender(), senderProfit, 
				gameState == GameState.WHITE_WON);
		updatePlayer(gameEntity.getChallenge().getReceiver(), receiverProfit,
				gameState == GameState.BLACK_WON);
	}
	
	/** {@inheritDoc} */
	@Override
	public int calculatePlayersProfit(UserEntity user, UserEntity opponent) {
		return calculateProfit(user, opponent, true);
	}

	/** {@inheritDoc} */
	@Override
	public int calculatePlayersLoss(UserEntity user, UserEntity opponent) {
		return calculateProfit(user, opponent, false);
	}

	private void updatePlayer(UserEntity player, int profit, boolean won) {
		player.setPoints(Math.max(player.getPoints()+profit, 0));
		updateLevel(player, won);
		userDao.update(player);
	}
	
	private int calculateProfit(UserEntity player, UserEntity opponent, boolean won) {
		
		int base = calculateBase(player, opponent, won);
		int bonus = calculateBonus(player, opponent, base);
		
		if (won) {
			int profit = base + bonus;
			return profit;
		}
		else {
			int loss = base - bonus;
			return loss*(-1);
		}
	}
	
	private int calculateBase(UserEntity player, UserEntity opponent, boolean won) {
		
		int level = player.getLevel().getValue();
		
		if (won) {
			int levelDifference = opponent.getLevel().getValue() - level;
			return WIN_BASES.get(levelDifference) * level;
		}
		else  {
			int levelDifference = level- opponent.getLevel().getValue();
			return LOSS_BASES.get(levelDifference) * level;
		}
	}
	
	private int calculateBonus(UserEntity player, UserEntity opponent, int base) {
		double playerProgress = calculateProgress(player);
		double opponentProgress = calculateProgress(opponent);
		
		double bonus = (opponentProgress - playerProgress) * 0.5 * base;
		return (int) Math.floor(bonus);
	}

	private double calculateProgress(UserEntity player) {
		
		List<GameEntity> gameEntities = GameUtils.extractGamesByStates(player.getSentChallenges(),
				player.getReceivedChallenges(), EnumSet.of(GameState.WHITE_WON, 
						GameState.BLACK_WON, GameState.DRAW));
		
		Level level = player.getLevel();
		int gamesPlayed = gameEntities.size();
		int gamesWon = GameUtils.calcultateWins(player.getId(), gameEntities);
		
		if (level.getValue() == Level.values().length
				|| gamesPlayed == 0) {
			return 0.0;
		}
		
		Level nextLevel = Level.getLevelByValue(level.getValue()+1);
		double winsPercentage = ((double) gamesWon) / gamesPlayed;
		
		double pointsProgress = Math.min(
				((double) player.getPoints() - level.getPointsRequired())
				/ (nextLevel.getPointsRequired() - level.getPointsRequired()), 
				1.0);
		double gamesProgress = Math.min(
				((double) gamesPlayed - level.getGamesRequired())
				/ (nextLevel.getGamesRequired() - level.getGamesRequired()), 
				1.0);
		double winsProgress = Math.min(
				((double) winsPercentage - level.getWinsRequired())
				/ (nextLevel.getWinsRequired() - level.getWinsRequired()), 
				1.0);
			
		return (pointsProgress + gamesProgress + winsProgress) / 3;
	}
	
	private void updateLevel(UserEntity player, boolean won) {
		
		List<GameEntity> gameEntities = GameUtils.extractGamesByStates(player.getSentChallenges(),
				player.getReceivedChallenges(), EnumSet.of(GameState.WHITE_WON, 
						GameState.BLACK_WON, GameState.DRAW));
		
		int gamesPlayed = gameEntities.size()+1;
		int gamesWon = GameUtils.calcultateWins(player.getId(), gameEntities);
		if (won) {
			gamesWon++;
		}
		double winsPercentage = ((double) gamesWon) / gamesPlayed;
		
		for (Level level : Level.values()) {
			if (player.getPoints() >= level.getPointsRequired()
					&& gamesPlayed >= level.getGamesRequired()
					&& winsPercentage >= level.getWinsRequired()) {
				player.setLevel(level);
			}
		}
	}
}
