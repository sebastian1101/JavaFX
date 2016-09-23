package com.capgemini.chess.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.chess.dataaccess.dao.GameDao;
import com.capgemini.chess.dataaccess.dao.MoveDao;
import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.entities.ChallengeEntity;
import com.capgemini.chess.dataaccess.entities.CoordinateEntity;
import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.dataaccess.entities.MoveEntity;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.GameState;
import com.capgemini.chess.dataaccess.enumerations.MoveType;
import com.capgemini.chess.dataaccess.enumerations.Piece;
import com.capgemini.chess.service.enumerations.BoardState;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.exceptions.InvalidMoveException;
import com.capgemini.chess.service.exceptions.InvalidUserException;
import com.capgemini.chess.service.impl.GameServiceImpl;
import com.capgemini.chess.service.to.BoardTO;
import com.capgemini.chess.service.to.CoordinateTO;
import com.capgemini.chess.service.to.GameTO;
import com.capgemini.chess.service.to.MoveTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class GameServiceTest {

	@Autowired
	GameService service;

	@Autowired
	GameDao gameDao;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	MoveDao moveDao;
	
	@Autowired
	RankService rankService;
	
	@Configuration
	static class GameServiceTestContextConfiguration {
		@Bean
		public GameService gameService() {
			return new GameServiceImpl();
		}
		
		@Bean
		public GameDao gameDao() {
			return Mockito.mock(GameDao.class);
		}
		
		@Bean
		public UserDao userDao() {
			return Mockito.mock(UserDao.class);
		}
		
		@Bean
		public MoveDao moveDao() {
			return Mockito.mock(MoveDao.class);
		}
		
		@Bean
		public RankService rankService() {
			return Mockito.mock(RankService.class);
		}
	}
	
	@Before
    public void setup() {		
        Mockito.reset(gameDao, rankService);
    }


	@Test
	public void testPerformMoveRegular() throws Exception {
		// given
		long userId = 1L;
		MoveTO move = new MoveTO();
		move.setFrom(new CoordinateTO(1, 1));
		move.setTo(new CoordinateTO(1, 2));
		move.setType(MoveType.ATTACK);
		
		GameEntity game = prepareGameEntity();

		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		BoardTO result = service.performMove(game.getId(), userId, move);
		
		// then
		Mockito.verify(gameDao).update(game);
		
		MoveTO moveResult = result.getMoveHistory().get(result.getMoveHistory().size() - 1);
		assertEquals(move.getFrom(), moveResult.getFrom());
		assertEquals(move.getTo(), moveResult.getTo());
		assertEquals(move.getType(), moveResult.getType());
		
		assertEquals(GameState.IN_PROGRESS, game.getState());
	}
	
	@Test
	public void testPerformMoveEntityNotFound() {
		// given
		long userId = 1L;
		MoveTO move = new MoveTO();
		move.setFrom(new CoordinateTO(1, 1));
		move.setTo(new CoordinateTO(2, 2));

		Mockito.when(gameDao.get(1L)).thenReturn(null);
		
		// when
		boolean expectedException = false;
		try {
			service.performMove(1L, userId, move);
		} catch (EntityNotFoundException e) {
			expectedException = true;
		} catch (InvalidUserException e) {
			expectedException = false;
		} catch (InvalidMoveException e) {
			expectedException = false;
		}
		
		// then
		assertTrue(expectedException);
	}
	
	@Test
	public void testPerformMoveInvalidUser() throws Exception {
		// given
		long userId = 1L;
		MoveTO move = new MoveTO();
		move.setFrom(new CoordinateTO(1, 1));
		move.setTo(new CoordinateTO(2, 2));

		GameEntity game = prepareGameEntity();

		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		boolean expectedException = false;
		try {
			service.performMove(1L, userId, move);
		} catch (EntityNotFoundException e) {
			expectedException = false;
		} catch (InvalidUserException e) {
			expectedException = false;
		} catch (InvalidMoveException e) {
			expectedException = true;
		}
		
		// then
		assertTrue(expectedException);
	}

	@Test
	public void testPerformMoveInvalidMove() {
		// given
		long userId = 2L;
		MoveTO move = new MoveTO();
		move.setFrom(new CoordinateTO(1, 1));
		move.setTo(new CoordinateTO(2, 2));

		GameEntity game = prepareGameEntity();

		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		boolean expectedException = false;
		try {
			service.performMove(1L, userId, move);
		} catch (EntityNotFoundException e) {
			expectedException = false;
		} catch (InvalidUserException e) {
			expectedException = true;
		} catch (InvalidMoveException e) {
			expectedException = false;
		}
		
		// then
		assertTrue(expectedException);
	}
	
	@Test
	public void testGetBoardSuccessful() throws Exception {
		// given
		GameEntity game = prepareGameEntity();
		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		BoardState state = BoardState.REGULAR;
		
		// when
		BoardTO board = service.getBoard(game.getId());
		
		// then
		Mockito.verify(gameDao).get(game.getId());
		
		assertEquals(state, board.getState());
	}
	
	@Test
	public void testGetBoardEntityNotFound() {
		// given
		GameEntity game = prepareGameEntity();
		Mockito.when(gameDao.get(game.getId())).thenReturn(null);
		
		// when
		boolean expectedException = false;
		try {
			service.getBoard(game.getId());
		} catch (EntityNotFoundException e) {
			expectedException = true;
		}
		
		// then
		assertTrue(expectedException);
	}
	
	@Test
	public void testGetBoardHistorySuccessful() throws Exception {
		// given
		GameEntity game = prepareGameEntity();
		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		BoardTO board = service.getBoardHistory(game.getId(), 0L);
		
		// then
		Mockito.verify(gameDao).get(game.getId());
		
		assertEquals(Piece.WHITE_ROOK, board.getPieces()[0][0]);
	}
	
	@Test
	public void testGetBoardHistoryEntityNotFound() {
		// given
		GameEntity game = prepareGameEntity();
		Mockito.when(gameDao.get(game.getId())).thenReturn(null);
		
		// when
		boolean expectedException = false;
		try {
			service.getBoardHistory(game.getId(), 0L);
		} catch (EntityNotFoundException e) {
			expectedException = true;
		}
		
		// then
		assertTrue(expectedException);
	}
	
	@Test
	public void testSuggestTieSuccessful() throws Exception {
		// given
		long userId = 1L;

		GameEntity game = prepareGameEntity();

		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		service.suggestTie(game.getId(), userId);
		
		// then
		Mockito.verify(gameDao).update(game);

		assertEquals(GameState.TIE_SUGGESTED_WHITE, game.getState());
	}
	
	@Test
	public void testSuggestTieAutomaticDraw() throws Exception {
		// given
		long userId = 1L;

		GameEntity game = prepareGameEntity();
		game.getMoves().addAll(prepareMoves());

		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		service.suggestTie(game.getId(), userId);
		
		// then
		Mockito.verify(gameDao).update(game);

		assertEquals(GameState.DRAW, game.getState());
	}
	
	@Test
	public void testSuggestTieEntityNotFound() {
		// given
		long userId = 1L;

		GameEntity game = prepareGameEntity();

		Mockito.when(gameDao.get(game.getId())).thenReturn(null);
		
		// when
		boolean expectedException = false;
		try {
			service.suggestTie(game.getId(), userId);
		} catch (EntityNotFoundException e) {
			expectedException = true;
		} catch (InvalidUserException e) {
			expectedException = false;
		}
		
		// then
		assertTrue(expectedException);
	}
	
	@Test
	public void testSuggestTieInvalidUser() {
		// given
		long userId = 3L;

		GameEntity game = prepareGameEntity();

		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		boolean expectedException = false;
		try {
			service.suggestTie(game.getId(), userId);
		} catch (EntityNotFoundException e) {
			expectedException = false;
		} catch (InvalidUserException e) {
			expectedException = true;
		}
		
		// then
		assertTrue(expectedException);
	}
	
	@Test
	public void testAcceptTieSuccessful() throws Exception {
		// given
		long userId = 1L;

		GameEntity game = prepareGameEntity();
		game.setState(GameState.TIE_SUGGESTED_BLACK);

		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		service.acceptTie(game.getId(), userId);
		
		// then
		Mockito.verify(gameDao).update(game);

		assertEquals(GameState.DRAW, game.getState());

	}
	
	@Test
	public void testAcceptTieNoTieSuggested() throws Exception {
		// given
		long userId = 1L;

		GameEntity game = prepareGameEntity();
		game.setState(GameState.IN_PROGRESS);

		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		service.acceptTie(game.getId(), userId);
		
		// then
		Mockito.verify(gameDao, Mockito.never()).update(game);

		assertEquals(GameState.IN_PROGRESS, game.getState());
	}
	
	@Test
	public void testAcceptTieEntityNotFound() {
		// given
		long userId = 1L;

		GameEntity game = prepareGameEntity();

		Mockito.when(gameDao.get(game.getId())).thenReturn(null);
		
		// when
		boolean expectedException = false;
		try {
			service.acceptTie(game.getId(), userId);
		} catch (EntityNotFoundException e) {
			expectedException = true;
		} catch (InvalidUserException e) {
			expectedException = false;
		}
		
		// then
		assertTrue(expectedException);
	}
	
	@Test
	public void testAcceptTieInvalidUser() {
		// given
		long userId = 3L;

		GameEntity game = prepareGameEntity();

		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		boolean expectedException = false;
		try {
			service.acceptTie(game.getId(), userId);
		} catch (EntityNotFoundException e) {
			expectedException = false;
		} catch (InvalidUserException e) {
			expectedException = true;
		}
		
		// then
		assertTrue(expectedException);		
	}
	
	@Test
	public void testGiveupGameSuccessful() throws Exception {
		// given
		long userId = 1L;

		GameEntity game = prepareGameEntity();
		game.setState(GameState.IN_PROGRESS);

		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		service.giveupGame(game.getId(), userId);
		
		// then
		Mockito.verify(gameDao).update(game);

		assertEquals(GameState.BLACK_WON, game.getState());
	}
	
	@Test
	public void testGiveupGameEntityNotFound() {
		// given
		long userId = 1L;

		GameEntity game = prepareGameEntity();

		Mockito.when(gameDao.get(game.getId())).thenReturn(null);
		
		// when
		boolean expectedException = false;
		try {
			service.giveupGame(game.getId(), userId);
		} catch (EntityNotFoundException e) {
			expectedException = true;
		} catch (InvalidUserException e) {
			expectedException = false;
		}
		
		// then
		assertTrue(expectedException);
	}
	
	@Test
	public void testGiveupGameInvalidUser() {
		// given
		long userId = 3L;

		GameEntity game = prepareGameEntity();

		Mockito.when(gameDao.get(game.getId())).thenReturn(game);
		
		// when
		boolean expectedException = false;
		try {
			service.giveupGame(game.getId(), userId);
		} catch (EntityNotFoundException e) {
			expectedException = false;
		} catch (InvalidUserException e) {
			expectedException = true;
		}
		
		// then
		assertTrue(expectedException);		
		
	}
	
	@Test
	public void testGetGames() {
		// given
		long userId = 1L;
		UserEntity player = new UserEntity();
		player.setId(userId);
		player.setSentChallenges(Arrays.asList(prepareChallengeEntity(), 
				prepareChallengeEntity(), prepareChallengeEntity()));
		
		Mockito.when(userDao.get(userId)).thenReturn(player);
		
		// when
		List<GameTO> games = service.getGames(userId);
		
		// then
		assertEquals(3, games.size());
	}
	
	private GameEntity prepareGameEntity() {
		UserEntity player = new UserEntity();
		player.setId(1L);
		UserEntity opponent = new UserEntity();
		opponent.setId(2L);
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setSender(player);
		challenge.setReceiver(opponent);
		GameEntity game = new GameEntity();
		game.setId(1L);
		game.setChallenge(challenge);
		game.setState(GameState.IN_PROGRESS);
		
		return game;
	}
	
	private List<MoveEntity> prepareMoves() {
		List<MoveEntity> moves = new ArrayList<>();
		for (int i = 0; i < 120; i++) {
			MoveEntity move = new MoveEntity();
			move.setFrom(new CoordinateEntity(0, 0));
			move.setTo(new CoordinateEntity(0, 0));
			move.setMovedPiece(Piece.BLACK_ROOK);
			move.setType(MoveType.ATTACK);
			moves.add(move);
		}
		return moves;
	}
	
	private ChallengeEntity prepareChallengeEntity() {
		ChallengeEntity challengeEntity = new ChallengeEntity();
		challengeEntity.setId(1L);
		challengeEntity.setGame(prepareGameEntity());
		return challengeEntity;
	}
}
