package com.capgemini.chess.service.impl;

import java.util.EnumSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.chess.dataaccess.dao.GameDao;
import com.capgemini.chess.dataaccess.dao.MoveDao;
import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.dataaccess.entities.MoveEntity;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.GameState;
import com.capgemini.chess.service.GameService;
import com.capgemini.chess.service.RankService;
import com.capgemini.chess.service.enumerations.BoardState;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.exceptions.InvalidMoveException;
import com.capgemini.chess.service.exceptions.InvalidUserException;
import com.capgemini.chess.service.manager.BoardManager;
import com.capgemini.chess.service.mapper.GameMapper;
import com.capgemini.chess.service.mapper.MoveMapper;
import com.capgemini.chess.service.to.BoardTO;
import com.capgemini.chess.service.to.GameTO;
import com.capgemini.chess.service.to.MoveTO;
import com.capgemini.chess.service.utils.GameUtils;

/**
 * Implementation of {@link GameService}.
 * 
 * @author Michal Bejm
 *
 */
@Service
public class GameServiceImpl implements GameService {

	@Autowired
	private GameDao gameDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private MoveDao moveDao;
	@Autowired
	private RankService rankService;

	/** {@inheritDoc} */
	@Override
	public BoardTO performMove(Long id, Long userId, MoveTO moveTO) 
			throws EntityNotFoundException, InvalidUserException, InvalidMoveException {
		
		GameEntity gameEntity = gameDao.get(id);
		
		if (gameEntity == null) {
			throw new EntityNotFoundException();
		}
		else {
			if (gameEntity.getState() == GameState.DRAW || gameEntity.getState() == GameState.BLACK_WON
					|| gameEntity.getState() == GameState.WHITE_WON) {
				throw new InvalidMoveException();
			}
			if (gameEntity.getMoves().size() % 2 == 0 &&
					gameEntity.getChallenge().getSender().getId() != userId) {
				throw new InvalidUserException();
			}
			else if (gameEntity.getMoves().size() % 2 == 1 &&
					gameEntity.getChallenge().getReceiver().getId() != userId) {
				throw new InvalidUserException();
			}
		}
		
		BoardManager boardManager = new BoardManager(MoveMapper.map2TOs(gameEntity.getMoves()));
		MoveTO move = boardManager.performMove(moveTO.getFrom(), moveTO.getTo());
		move.setSequenceNumber(boardManager.getBoard().getMoveHistory().size()+1);
		BoardState boardState = boardManager.updateBoardState();
		
		MoveEntity moveEntity = MoveMapper.map(move);
		moveEntity = moveDao.create(moveEntity);
		gameEntity.getMoves().add(moveEntity);
		
		if (boardState == BoardState.CHECK_MATE) {
			GameState gameState;
			if (gameEntity.getMoves().size() % 2 == 1) {
				gameState = GameState.WHITE_WON;
			}
			else {
				gameState = GameState.BLACK_WON;
			}
			rankService.updatePlayersStats(gameEntity, gameState);
			gameEntity.setState(gameState);
		}
		else if (boardState == BoardState.STALE_MATE) {
			rankService.updatePlayersStats(gameEntity, GameState.DRAW);
			gameEntity.setState(GameState.DRAW);
		}
		
		gameDao.update(gameEntity);
		return boardManager.getBoard();
	}

	/** {@inheritDoc} */
	@Override
	public BoardTO getBoard(Long id) throws EntityNotFoundException {
		GameEntity gameEntity = gameDao.get(id);
		
		if (gameEntity == null) {
			throw new EntityNotFoundException();
		}
		
		BoardManager boardManager = new BoardManager(MoveMapper.map2TOs(gameEntity.getMoves()));
		boardManager.updateBoardState();
		
		return boardManager.getBoard();
	}

	@Override
	public BoardTO getBoardHistory(Long gameId, Long moveNumber) throws EntityNotFoundException {
		GameEntity gameEntity = gameDao.get(gameId);
		
		if (gameEntity == null) {
			throw new EntityNotFoundException();
		}
		
		List<MoveTO> moves = MoveMapper.map2TOs(gameEntity.getMoves().subList(0, 
				Math.min(gameEntity.getMoves().size(), moveNumber.intValue())));
		BoardManager boardManager = new BoardManager(moves);
		boardManager.updateBoardState();
		
		return boardManager.getBoard();
	}

	/** {@inheritDoc} */
	@Override
	public void suggestTie(Long id, Long userId) throws EntityNotFoundException, InvalidUserException {
		GameEntity gameEntity = gameDao.get(id);
		
		if (gameEntity == null) {
			throw new EntityNotFoundException();
		}
		
		BoardManager boardManager = new BoardManager(MoveMapper.map2TOs(gameEntity.getMoves()));
		
		if (doesGameContainUser(userId, gameEntity)) {
			throw new InvalidUserException();
		}
		else if (boardManager.checkFiftyMoveRule() || boardManager.checkThreefoldRepetitionRule()) {
			drawGame(gameEntity);
		}
		else if (gameEntity.getChallenge().getSender().getId() == userId) {
			gameEntity.setState(GameState.TIE_SUGGESTED_WHITE);
			gameEntity = gameDao.update(gameEntity);
		}
		else if (gameEntity.getChallenge().getReceiver().getId() == userId) {
			gameEntity.setState(GameState.TIE_SUGGESTED_BLACK);
			gameEntity = gameDao.update(gameEntity);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void acceptTie(Long id, Long userId) throws EntityNotFoundException, InvalidUserException {
		GameEntity gameEntity = gameDao.get(id);
		
		if (gameEntity == null) {
			throw new EntityNotFoundException();
		}

		if (doesGameContainUser(userId, gameEntity)) {
			throw new InvalidUserException();
		}
		else if (gameEntity.getChallenge().getSender().getId() == userId && 
				gameEntity.getState() == GameState.TIE_SUGGESTED_BLACK) {
			drawGame(gameEntity);
		}
		else if (gameEntity.getChallenge().getReceiver().getId() == userId&& 
				gameEntity.getState() == GameState.TIE_SUGGESTED_WHITE) {
			drawGame(gameEntity);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void giveupGame(Long id, Long userId) throws EntityNotFoundException, InvalidUserException {
		GameEntity gameEntity = gameDao.get(id);
		
		if (gameEntity == null) {
			throw new EntityNotFoundException();
		}
		
		GameState gameState = null;
		if (doesGameContainUser(userId, gameEntity)) {
			throw new InvalidUserException();
		}
		else if (gameEntity.getChallenge().getSender().getId() == userId) {
			gameState = GameState.BLACK_WON;
		}
		else if (gameEntity.getChallenge().getReceiver().getId() == userId) {
			gameState = GameState.WHITE_WON;
		}
		
		rankService.updatePlayersStats(gameEntity, gameState);
		gameEntity.setState(gameState);
		gameEntity = gameDao.update(gameEntity);
	}

	/** {@inheritDoc} */
	@Override
	public List<GameTO> getGames(Long userId) {
		UserEntity user = userDao.get(userId);
		List<GameEntity> games = GameUtils.extractGamesByStates(user.getSentChallenges(),
				user.getReceivedChallenges(),EnumSet.of(GameState.IN_PROGRESS, 
						GameState.TIE_SUGGESTED_BLACK, GameState.TIE_SUGGESTED_WHITE));
		return GameMapper.map2TOs(games);
	}
	
	private boolean doesGameContainUser(Long userId, GameEntity gameEntity) {
		return gameEntity.getChallenge().getSender().getId() != userId 
				&& gameEntity.getChallenge().getReceiver().getId() != userId;
	}
	
	private void drawGame(GameEntity gameEntity) {
		
		rankService.updatePlayersStats(gameEntity, GameState.DRAW);
		gameEntity.setState(GameState.DRAW);
		gameDao.update(gameEntity);
	}
}
