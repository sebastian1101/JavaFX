package com.capgemini.chess.service;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.Level;
import com.capgemini.chess.service.impl.StatisticsServiceImpl;
import com.capgemini.chess.service.to.UserStatsTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class StatisticsServiceTest {

	@Autowired
	StatisticsService service;

	@Autowired
	UserDao userDao;
	
	@Configuration
	static class StatisticsServiceTestContextConfiguration {
		@Bean
		public StatisticsService statisticsService() {
			return new StatisticsServiceImpl();
		}
		
		@Bean
		public UserDao userDao() {
			return Mockito.mock(UserDao.class);
		}
	}
	
	@Test
	public void testGetUserStats() {
		// given
		UserEntity player = new UserEntity();
		player.setId(1L);
		player.setLogin("user");
		player.setPoints(700);
		player.setLevel(Level.BEGINNER);
		player.setSentChallenges(ServiceTestUtils.createListOfGames(player, 10, 20, 30));
		Mockito.when(userDao.get(player.getId())).thenReturn(player);
		
		// when
		UserStatsTO userStats = service.getUserStats(player.getId());
		
		// then
		assertEquals(player.getId(), userStats.getId());
		assertEquals(player.getLevel(), userStats.getLevel());
		assertEquals(player.getLogin(), userStats.getLogin());
		assertEquals(player.getPoints(), userStats.getPoints());
		assertEquals(60, userStats.getGamesPlayed());
		assertEquals(10, userStats.getGamesDrawn());
		assertEquals(20, userStats.getGamesWon());
		assertEquals(30, userStats.getGamesLost());
	}
	
	@Test
	public void testReadLeaderboardForTopPlayer() {
		// given
		UserEntity player = new UserEntity();
		player.setId(1L);
		player.setLogin("user");
		player.setPoints(700);
		player.setLevel(Level.MIDDLEBROW);
		Mockito.when(userDao.get(player.getId())).thenReturn(player);
		Mockito.when(userDao.findPlayersPostionInRank(player.getLevel(), player.getPoints(), player.getId()))
			.thenReturn(2);
		Mockito.when(userDao.findTopUsers(0, 11)).thenReturn(
				Arrays.asList(new UserEntity(), new UserEntity(), new UserEntity(), new UserEntity()));
		
		// when
		List<UserStatsTO> leaderboard = service.readLeaderboard(1L);
		
		// then
		assertEquals(4, leaderboard.size());
	}
	
	@Test
	public void testReadLeaderboardForRegularPlayer() {
		// given
		UserEntity player = new UserEntity();
		player.setId(1L);
		player.setLogin("user");
		player.setPoints(700);
		player.setLevel(Level.MIDDLEBROW);
		Mockito.when(userDao.get(player.getId())).thenReturn(player);
		Mockito.when(userDao.findPlayersPostionInRank(player.getLevel(), player.getPoints(), player.getId()))
			.thenReturn(30);
		Mockito.when(userDao.findTopUsers(0, 3)).thenReturn(
				Arrays.asList(new UserEntity(), new UserEntity(), new UserEntity()));
		Mockito.when(userDao.findTopUsers(19, 21)).thenReturn(
				Arrays.asList(new UserEntity(), new UserEntity(), new UserEntity(), new UserEntity()));
		
		// when
		List<UserStatsTO> leaderboard = service.readLeaderboard(1L);
		
		// then
		assertEquals(7, leaderboard.size());
	}
}