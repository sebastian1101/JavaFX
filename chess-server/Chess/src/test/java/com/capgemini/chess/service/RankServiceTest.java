package com.capgemini.chess.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.entities.ChallengeEntity;
import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.GameState;
import com.capgemini.chess.dataaccess.enumerations.Level;
import com.capgemini.chess.service.impl.RankServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RankServiceTest {

	@Autowired
	RankService service;

	@Autowired
	UserDao userDao;
	
	@Configuration
	static class RankServiceTestContextConfiguration {
		@Bean
		public RankService rankService() {
			return new RankServiceImpl();
		}
		
		@Bean
		public UserDao userDao() {
			return Mockito.mock(UserDao.class);
		}
	}
    
	@Test
	public void testUpdatePlayersRankWonLevelNotChanged() {
		// given
		UserEntity player = new UserEntity();
		player.setId(1L);
		player.setPoints(700);
		player.setLevel(Level.BEGINNER);
		player.setSentChallenges(ServiceTestUtils.createListOfGames(player, 0, 20, 20));
		UserEntity opponent = new UserEntity();
		opponent.setId(2L);
		opponent.setPoints(2000);
		opponent.setLevel(Level.EXPERIENCED_BEGINNER);
		opponent.setSentChallenges(ServiceTestUtils.createListOfGames(opponent, 0, 30, 40));
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setSender(player);
		challenge.setReceiver(opponent);
		GameEntity game = new GameEntity();
		game.setChallenge(challenge);
		
		// when
		service.updatePlayersStats(game, GameState.WHITE_WON);
		
		// then
		assertEquals(Level.BEGINNER, player.getLevel());
		assertEquals(956, player.getPoints());
	}
	
	@Test
	public void testUpdatePlayersRankDrawLevelNotChanged() {
		// given
		UserEntity player = new UserEntity();
		player.setId(1L);
		player.setPoints(30000);
		player.setLevel(Level.PROFESSIONAL);
		player.setSentChallenges(ServiceTestUtils.createListOfGames(player, 20, 180, 100));
		UserEntity opponent = new UserEntity();
		opponent.setId(2L);
		opponent.setPoints(30000);
		opponent.setLevel(Level.PROFESSIONAL);
		opponent.setSentChallenges(ServiceTestUtils.createListOfGames(opponent, 20, 180, 10));
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setSender(player);
		challenge.setReceiver(opponent);
		GameEntity game = new GameEntity();
		game.setChallenge(challenge);
		
		// when
		service.updatePlayersStats(game, GameState.DRAW);
		
		// then
		assertEquals(Level.PROFESSIONAL, player.getLevel());
		assertEquals(30000, player.getPoints());
	}
	
	@Test
	public void testUpdatePlayersRankLostLevelNotChanged() {
		// given
		UserEntity player = new UserEntity();
		player.setId(1L);
		player.setPoints(11111);
		player.setLevel(Level.ADVANCED);
		player.setSentChallenges(ServiceTestUtils.createListOfGames(player, 0, 100, 100));
		UserEntity opponent = new UserEntity();
		opponent.setId(2L);
		opponent.setPoints(10000);
		opponent.setLevel(Level.EXPERIENCED_MIDDLEBORW);
		opponent.setSentChallenges(ServiceTestUtils.createListOfGames(opponent, 10, 100, 40));
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setSender(player);
		challenge.setReceiver(opponent);
		GameEntity game = new GameEntity();
		game.setChallenge(challenge);
		
		// when
		service.updatePlayersStats(game, GameState.BLACK_WON);
		
		// then
		assertEquals(Level.ADVANCED, player.getLevel());
		assertEquals(10812, player.getPoints());
	}
	
	@Test
	public void testUpdatePlayersRankWonLevelChanged() {
		// given
		UserEntity player = new UserEntity();
		player.setId(1L);
		player.setPoints(2000);
		player.setLevel(Level.BEGINNER);
		player.setSentChallenges(ServiceTestUtils.createListOfGames(player, 4, 40, 0));
		UserEntity opponent = new UserEntity();
		opponent.setId(2L);
		opponent.setPoints(10000);
		opponent.setLevel(Level.ADVANCED);
		opponent.setSentChallenges(ServiceTestUtils.createListOfGames(opponent, 0, 100, 100));
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setSender(opponent);
		challenge.setReceiver(player);
		GameEntity game = new GameEntity();
		game.setChallenge(challenge);
		
		// when
		service.updatePlayersStats(game, GameState.BLACK_WON);
		
		// then
		assertEquals(Level.EXPERIENCED_BEGINNER, player.getLevel());
		assertEquals(3164, player.getPoints());
	}
	
	@Test
	public void testUpdatePlayersRankDrawLevelChanged() {
		// given
		UserEntity player = new UserEntity();
		player.setId(1L);
		player.setPoints(400);
		player.setLevel(Level.WEAKLING);
		player.setSentChallenges(ServiceTestUtils.createListOfGames(player, 0, 8, 92));
		UserEntity opponent = new UserEntity();
		opponent.setId(2L);
		opponent.setPoints(350);
		opponent.setLevel(Level.WEAKLING);
		opponent.setSentChallenges(ServiceTestUtils.createListOfGames(opponent, 2, 4, 4));
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setSender(opponent);
		challenge.setReceiver(player);
		GameEntity game = new GameEntity();
		game.setChallenge(challenge);
		
		// when
		service.updatePlayersStats(game, GameState.DRAW);
		
		// then
		assertEquals(Level.NEWBIE, player.getLevel());
		assertEquals(400, player.getPoints());
	}
	
	@Test
	public void testUpdatePlayersRankLostLevelChanged() {
		// given
		UserEntity player = new UserEntity();
		player.setId(1L);
		player.setPoints(20000);
		player.setLevel(Level.ADVANCED);
		player.setSentChallenges(ServiceTestUtils.createListOfGames(player, 0, 100, 50));
		UserEntity opponent = new UserEntity();
		opponent.setId(2L);
		opponent.setPoints(0);
		opponent.setLevel(Level.NEWBIE);
		opponent.setSentChallenges(ServiceTestUtils.createListOfGames(opponent, 0, 0, 0));
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setSender(opponent);
		challenge.setReceiver(player);
		GameEntity game = new GameEntity();
		game.setChallenge(challenge);
		
		// when
		service.updatePlayersStats(game, GameState.WHITE_WON);
		
		// then
		assertEquals(Level.MIDDLEBROW, player.getLevel());
		assertEquals(3113, player.getPoints());
	}

	@Test
	public void testUpdatePlayersRankWonMaxLevel() {
		// given
		UserEntity player = new UserEntity();
		player.setId(1L);
		player.setPoints(100000);
		player.setLevel(Level.CHUCK_NORRIS_OF_CHESS);
		player.setSentChallenges(ServiceTestUtils.createListOfGames(player, 0, 1000, 0));
		UserEntity opponent = new UserEntity();
		opponent.setId(2L);
		opponent.setPoints(50000);
		opponent.setLevel(Level.MASTER);
		opponent.setSentChallenges(ServiceTestUtils.createListOfGames(opponent, 50, 400, 50));
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setSender(player);
		challenge.setReceiver(opponent);
		GameEntity game = new GameEntity();
		game.setChallenge(challenge);
		
		// when
		service.updatePlayersStats(game, GameState.WHITE_WON);
		
		// then
		assertEquals(Level.CHUCK_NORRIS_OF_CHESS, player.getLevel());
		assertEquals(100276, player.getPoints());
	}
	
	@Test
	public void testUpdatePlayersRankAllPointsLost() {
		// given
		UserEntity player = new UserEntity();
		player.setId(1L);
		player.setPoints(100000);
		player.setLevel(Level.CHUCK_NORRIS_OF_CHESS);
		player.setSentChallenges(ServiceTestUtils.createListOfGames(player, 0, 450, 50));
		UserEntity opponent = new UserEntity();
		opponent.setId(2L);
		opponent.setPoints(0);
		opponent.setLevel(Level.NEWBIE);
		opponent.setSentChallenges(ServiceTestUtils.createListOfGames(opponent, 0, 0, 0));
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setSender(player);
		challenge.setReceiver(opponent);
		GameEntity game = new GameEntity();
		game.setChallenge(challenge);
		
		// when
		service.updatePlayersStats(game, GameState.BLACK_WON);
		
		// then
		assertEquals(Level.NEWBIE, player.getLevel());
		assertEquals(0, player.getPoints());
	}
}
