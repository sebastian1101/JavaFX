package com.capgemini.chess.service.impl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.chess.dataaccess.dao.ChallengeDao;
import com.capgemini.chess.dataaccess.dao.GameDao;
import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.entities.ChallengeEntity;
import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.GameState;
import com.capgemini.chess.dataaccess.enumerations.Level;
import com.capgemini.chess.service.ChallengeService;
import com.capgemini.chess.service.RankService;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.exceptions.InvalidUserException;
import com.capgemini.chess.service.mapper.UserStatsMapper;
import com.capgemini.chess.service.to.ChallengeLineTO;
import com.capgemini.chess.service.to.PendingChallengesTO;
import com.capgemini.chess.service.to.UserStatsTO;

/**
 * Implementation of {@link ChallengeService}.
 * 
 * @author Michal Bejm
 *
 */
@Service
public class ChallengeServiceImpl implements ChallengeService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ChallengeDao challengeDao;
	
	@Autowired
	private GameDao gameDao;
	
	@Autowired
	private RankService rankService;

	/** {@inheritDoc} */
	@Override
	public List<ChallengeLineTO> getChallengeSuggestions(Long userId) {
		
		UserEntity user = userDao.get(userId);
		
		EnumSet<Level> similarLevels = determineSimilarLevels(user.getLevel());
		List<UserEntity> suggestedUsers = userDao.findUsersByLevels(similarLevels, userId);
		
		List<ChallengeLineTO> challengeSuggestions = new ArrayList<>();
		for (UserEntity suggestedUser : suggestedUsers) {
			ChallengeLineTO newChallenge = new ChallengeLineTO();
			
			newChallenge.setOpponent(UserStatsMapper.map(suggestedUser));
			newChallenge.setProfit(rankService.calculatePlayersProfit(user, suggestedUser));
			newChallenge.setLoss(rankService.calculatePlayersLoss(user, suggestedUser));
			
			challengeSuggestions.add(newChallenge);
		}
		
		return challengeSuggestions;
	}

	/** {@inheritDoc} */
	@Override
	public PendingChallengesTO getPendingChallenges(Long userId) {
		PendingChallengesTO pendingChallenges = new PendingChallengesTO();
		UserEntity userEntity = userDao.get(userId);
		
		for (ChallengeEntity challengeEntity : userEntity.getReceivedChallenges()) {
			if (challengeEntity.getGame() == null) {
				ChallengeLineTO challengeLine = createChallengeLineForReceiver(challengeEntity);
				pendingChallenges.getReceivedChallenges().add(challengeLine);
			}
		}
		
		for (ChallengeEntity challengeEntity : userEntity.getSentChallenges()) {
			if (challengeEntity.getGame() == null) {
				ChallengeLineTO challengeLine = createChallengeLineForSender(challengeEntity);
				pendingChallenges.getSentChallenges().add(challengeLine);
			}
		}
		
		return pendingChallenges;
	}

	/** {@inheritDoc} */
	@Override
	public ChallengeLineTO createChallenge(Long receiverId, Long userId) 
			throws EntityNotFoundException {
		
		UserEntity sender = userDao.get(userId);
		UserEntity receiver = userDao.get(receiverId);
		
		if (sender == null || receiver == null) {
			throw new EntityNotFoundException();
		}
		
		ChallengeEntity challengeEntity = new ChallengeEntity();
		challengeEntity.setSender(sender);
		challengeEntity.setReceiver(receiver);
		challengeEntity.setSenderLevel(sender.getLevel());
		challengeEntity.setReceiverLevel(receiver.getLevel());
		
		challengeEntity = challengeDao.create(challengeEntity);
		
		sender.getSentChallenges().add(challengeEntity);
		userDao.update(sender);
		receiver.getReceivedChallenges().add(challengeEntity);
		userDao.update(receiver);
		
		ChallengeLineTO challengeLine = createChallengeLineForSender(challengeEntity);
		
		return challengeLine;
	}

	/** {@inheritDoc} */
	@Override
	public void acceptChallenge(Long id, Long userId) 
		throws EntityNotFoundException, InvalidUserException {
		
		ChallengeEntity challengeEntity = challengeDao.get(id);
		
		if (challengeEntity == null) {
			throw new EntityNotFoundException();
		}
		
		if (challengeEntity.getReceiver().getId() == userId) {
			if (challengeEntity.getReceiver().getLevel() != challengeEntity.getReceiverLevel()
					|| challengeEntity.getSender().getLevel() != challengeEntity.getSenderLevel()) {
				declineChallenge(id, userId);
			}
			else {
				acceptChallenge(challengeEntity);
			}
		}
		else {
			throw new InvalidUserException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void declineChallenge(Long id, Long userId) 
		throws EntityNotFoundException, InvalidUserException {
		
		ChallengeEntity challengeEntity = challengeDao.get(id);
		
		if (challengeEntity == null) {
			throw new EntityNotFoundException();
		}
		
		if (challengeEntity.getReceiver().getId() == userId) {
			challengeDao.delete(id);
		}
		else {
			throw new InvalidUserException();
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public UserStatsTO findChallengedPlayer(String login) throws EntityNotFoundException {
		UserEntity userEntity = userDao.findUsersByLogin(login);
		
		if (userEntity == null) {
			throw new EntityNotFoundException();
		}
		
		return UserStatsMapper.map(userEntity);
	}

	private void acceptChallenge(ChallengeEntity challengeEntity) {
		
		GameEntity newGame = new GameEntity();
		newGame.setChallenge(challengeEntity);
		newGame.setState(GameState.IN_PROGRESS);
		gameDao.create(newGame);
		
		challengeEntity.setGame(newGame);
		challengeDao.update(challengeEntity);
	}
	
	private EnumSet<Level> determineSimilarLevels(Level level) {
		
		Level fromLevel = Level.getLevelByValue(Math.max(level.getValue()-2, 1));
		Level toLevel = Level.getLevelByValue(Math.min(level.getValue()+2, 10));

		return EnumSet.range(fromLevel, toLevel);
	}
	
	private ChallengeLineTO createChallengeLineForSender(ChallengeEntity challengeEntity) {
		ChallengeLineTO challengeLine = new ChallengeLineTO();
		challengeLine.setId(challengeEntity.getId());
		challengeLine.setOpponent(UserStatsMapper.map(challengeEntity.getReceiver()));
		challengeLine.setProfit(rankService.calculatePlayersProfit(challengeEntity.getSender(), 
				challengeEntity.getReceiver()));
		challengeLine.setLoss(rankService.calculatePlayersLoss(challengeEntity.getSender(), 
				challengeEntity.getReceiver()));
		return challengeLine;
	}
	
	private ChallengeLineTO createChallengeLineForReceiver(ChallengeEntity challengeEntity) {
		ChallengeLineTO challengeLine = new ChallengeLineTO();
		challengeLine.setId(challengeEntity.getId());
		challengeLine.setOpponent(UserStatsMapper.map(challengeEntity.getSender()));
		challengeLine.setProfit(rankService.calculatePlayersProfit(challengeEntity.getReceiver(), 
				challengeEntity.getSender()));
		challengeLine.setLoss(rankService.calculatePlayersLoss(challengeEntity.getReceiver(), 
				challengeEntity.getSender()));
		return challengeLine;
	}
}
