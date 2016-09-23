package com.capgemini.chess.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.chess.dataaccess.dao.ChallengeDao;
import com.capgemini.chess.dataaccess.dao.GameDao;
import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.entities.ChallengeEntity;
import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.Level;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.exceptions.InvalidUserException;
import com.capgemini.chess.service.impl.ChallengeServiceImpl;
import com.capgemini.chess.service.to.ChallengeLineTO;
import com.capgemini.chess.service.to.PendingChallengesTO;
import com.capgemini.chess.service.to.UserStatsTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ChallengeServiceTest {

	@Autowired
	ChallengeService service;

	@Autowired
	ChallengeDao challengeDao;
	
	@Autowired
	GameDao gameDao;

	@Autowired
	UserDao userDao;
	
	@Autowired
	RankService rankService;
	
	@Configuration
	static class ChallengeServiceTestContextConfiguration {
		@Bean
		public ChallengeService challengeService() {
			return new ChallengeServiceImpl();
		}
		
		@Bean
		public ChallengeDao challengeDao() {
			return Mockito.mock(ChallengeDao.class);
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
		public RankService rankService() {
			return Mockito.mock(RankService.class);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Before
    public void setup() {
        Mockito.reset(challengeDao, gameDao, userDao);
    }

	@Test
	public void testGetChallengeSuggestions() {
		// given
		UserEntity userEntity = new UserEntity();
		userEntity.setAboutMe("about me");
		userEntity.setEmail("user@user.com");
		userEntity.setId(1L);
		userEntity.setLevel(Level.MASTER);
		userEntity.setLifeMotto("motto");
		userEntity.setLogin("user");
		userEntity.setName("name");
		userEntity.setPassword("password");
		userEntity.setPoints(1000);
		userEntity.setSurname("surname");
		
		Mockito.when(userDao.get(1L)).thenReturn(userEntity);
		Mockito.when(userDao.findUsersByLevels(Matchers.any(), Matchers.eq(1L))).thenReturn(
				Arrays.asList(new UserEntity(), new UserEntity(), new UserEntity()));
		
		// when
		List<ChallengeLineTO> challengeTOs = service.getChallengeSuggestions(userEntity.getId());
		
		// then
		assertNotNull(challengeTOs);
		assertEquals(3, challengeTOs.size());
	}
	
	@Test
	public void testGetPendingChallenges() {
		// given
		long id = 1L;
		UserEntity user = new UserEntity();
		user.setId(id);
		user.setSentChallenges(Arrays.asList(new ChallengeEntity(), new ChallengeEntity()));
		user.setReceivedChallenges(Arrays.asList(new ChallengeEntity(), 
				new ChallengeEntity(), new ChallengeEntity()));
		
		Mockito.when(userDao.get(id)).thenReturn(user);
		
		// when
		PendingChallengesTO pendingChallenges = service.getPendingChallenges(id);
		
		// then
		Mockito.verify(userDao).get(id);
		
		assertNotNull(pendingChallenges);
		assertEquals(2, pendingChallenges.getSentChallenges().size());
		assertEquals(3, pendingChallenges.getReceivedChallenges().size());
	}
	
	@Test
	public void testCreateChallengeSuccessful() throws Exception {
		// given
		UserEntity sender = new UserEntity();
		sender.setId(1L);
		sender.setLevel(Level.MASTER);
		sender.setLogin("white");
		sender.setPoints(1000);
		UserEntity receiver = new UserEntity();
		receiver.setId(2L);
		receiver.setLevel(Level.ADVANCED);
		receiver.setLogin("black");
		receiver.setPoints(2000);
		
		Mockito.when(userDao.get(sender.getId())).thenReturn(sender);
		Mockito.when(userDao.get(receiver.getId())).thenReturn(receiver);
		Mockito.when(challengeDao.create(Matchers.any(ChallengeEntity.class))).thenAnswer(
				new Answer<ChallengeEntity>() {
			@Override
			public ChallengeEntity answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (ChallengeEntity) args[0];
			}
		});
		
		// when
		ChallengeLineTO challenge = service.createChallenge(2L, 1L);
		
		// then
		Mockito.verify(challengeDao).create(Matchers.any(ChallengeEntity.class));
		Mockito.verify(userDao, Mockito.times(2)).update(Matchers.any(UserEntity.class));
		
		assertNotNull(challenge);
		assertEquals(Level.ADVANCED, challenge.getOpponent().getLevel());
		assertEquals("black", challenge.getOpponent().getLogin());
		assertEquals(2L, challenge.getOpponent().getId());
	}
	
	@Test
	public void testCreateChallengeUserNotFound() {
		// given
		UserEntity sender = new UserEntity();
		sender.setId(1L);
		sender.setLevel(Level.MASTER);
		sender.setLogin("white");
		sender.setPoints(1000);
		UserEntity receiver = new UserEntity();
		receiver.setId(2L);
		receiver.setLevel(Level.ADVANCED);
		receiver.setLogin("black");
		receiver.setPoints(2000);
		
		Mockito.when(userDao.get(sender.getId())).thenReturn(sender);
		Mockito.when(userDao.get(receiver.getId())).thenReturn(null);
		
		// when
		boolean excpextedExceptionThrown = false;
		try {
			service.createChallenge(2L, 1L);
		} catch (EntityNotFoundException e) {
			excpextedExceptionThrown = true;
		}
		
		// then
		assertTrue(excpextedExceptionThrown);
	}
	
	@Test
	public void testAcceptChallengeSuccessful() throws Exception {
		// given
		UserEntity sender = new UserEntity();
		sender.setId(1L);
		sender.setLevel(Level.MASTER);
		sender.setLogin("white");
		sender.setPoints(1000);
		UserEntity receiver = new UserEntity();
		receiver.setId(2L);
		receiver.setLevel(Level.ADVANCED);
		receiver.setLogin("black");
		receiver.setPoints(2000);
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setId(1L);
		challenge.setSender(sender);
		challenge.setSenderLevel(Level.MASTER);
		challenge.setReceiver(receiver);
		challenge.setReceiverLevel(Level.ADVANCED);
		
		Mockito.when(userDao.get(sender.getId())).thenReturn(sender);
		Mockito.when(userDao.get(receiver.getId())).thenReturn(receiver);
		Mockito.when(challengeDao.get(challenge.getId())).thenReturn(challenge);
		
		// when
		service.acceptChallenge(challenge.getId(), receiver.getId());
		
		// then
		Mockito.verify(challengeDao).update(Matchers.any(ChallengeEntity.class));
		Mockito.verify(gameDao).create(Matchers.any(GameEntity.class));
		
		assertNotNull(challenge.getGame());
		assertNotNull(challenge.getGame().getChallenge());
	}
	
	@Test
	public void testAcceptChallengeLevelChanged() throws Exception {
		// given
		UserEntity sender = new UserEntity();
		sender.setId(1L);
		sender.setLevel(Level.MASTER);
		sender.setLogin("white");
		sender.setPoints(1000);
		UserEntity receiver = new UserEntity();
		receiver.setId(2L);
		receiver.setLevel(Level.ADVANCED);
		receiver.setLogin("black");
		receiver.setPoints(2000);
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setId(1L);
		challenge.setSender(sender);
		challenge.setSenderLevel(Level.MASTER);
		challenge.setReceiver(receiver);
		challenge.setReceiverLevel(Level.EXPERIENCED_BEGINNER);
		
		Mockito.when(userDao.get(sender.getId())).thenReturn(sender);
		Mockito.when(userDao.get(receiver.getId())).thenReturn(receiver);
		Mockito.when(challengeDao.get(challenge.getId())).thenReturn(challenge);
		
		// when
		service.acceptChallenge(challenge.getId(), receiver.getId());
		
		// then
		Mockito.verify(challengeDao).delete(challenge.getId());
		
		assertNull(challenge.getGame());
	}
	
	@Test
	public void testAcceptChallengeNotFound() {
		// given
		long id = 1L;
		
		Mockito.when(challengeDao.get(id)).thenReturn(null);
		
		// when
		boolean excpextedExceptionThrown = false;
		try {
			service.acceptChallenge(id, 2L);
		} catch (EntityNotFoundException e) {
			excpextedExceptionThrown = true;
		} catch (InvalidUserException e) {
			excpextedExceptionThrown = false;
		}
		
		// then
		assertTrue(excpextedExceptionThrown);
	}
	
	@Test
	public void testAcceptChallengeInvalidUser() {
		// given
		UserEntity sender = new UserEntity();
		sender.setId(1L);
		sender.setLevel(Level.MASTER);
		sender.setLogin("white");
		sender.setPoints(1000);
		UserEntity receiver = new UserEntity();
		receiver.setId(2L);
		receiver.setLevel(Level.ADVANCED);
		receiver.setLogin("black");
		receiver.setPoints(2000);
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setId(1L);
		challenge.setSender(sender);
		challenge.setSenderLevel(Level.MASTER);
		challenge.setReceiver(receiver);
		challenge.setReceiverLevel(Level.ADVANCED);
		
		Mockito.when(userDao.get(sender.getId())).thenReturn(sender);
		Mockito.when(userDao.get(receiver.getId())).thenReturn(receiver);
		Mockito.when(challengeDao.get(challenge.getId())).thenReturn(challenge);
		
		// when
		boolean excpextedExceptionThrown = false;
		try {
			service.acceptChallenge(challenge.getId(), sender.getId());
		} catch (EntityNotFoundException e) {
			excpextedExceptionThrown = false;
		} catch (InvalidUserException e) {
			excpextedExceptionThrown = true;
		}
		
		// then
		assertTrue(excpextedExceptionThrown);
	}
	
	@Test
	public void testDeclineChallengeSuccessful() throws Exception {
		// given
		UserEntity sender = new UserEntity();
		sender.setId(1L);
		sender.setLevel(Level.MASTER);
		sender.setLogin("white");
		sender.setPoints(1000);
		UserEntity receiver = new UserEntity();
		receiver.setId(2L);
		receiver.setLevel(Level.ADVANCED);
		receiver.setLogin("black");
		receiver.setPoints(2000);
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setId(1L);
		challenge.setSender(sender);
		challenge.setSenderLevel(Level.MASTER);
		challenge.setReceiver(receiver);
		challenge.setReceiverLevel(Level.ADVANCED);
		
		Mockito.when(userDao.get(sender.getId())).thenReturn(sender);
		Mockito.when(userDao.get(receiver.getId())).thenReturn(receiver);
		Mockito.when(challengeDao.get(challenge.getId())).thenReturn(challenge);
		
		// when
		service.declineChallenge(challenge.getId(), receiver.getId());
		
		// then
		Mockito.verify(challengeDao).delete(challenge.getId());
		
		assertNull(challenge.getGame());
	}
	
	@Test
	public void testDeclineChallengeNotFound() {
		// given
		long id = 1L;
		
		Mockito.when(challengeDao.get(id)).thenReturn(null);
		
		// when
		boolean excpextedExceptionThrown = false;
		try {
			service.declineChallenge(id, 2L);
		} catch (EntityNotFoundException e) {
			excpextedExceptionThrown = true;
		} catch (InvalidUserException e) {
			excpextedExceptionThrown = false;
		}
		
		// then
		assertTrue(excpextedExceptionThrown);
	}
	
	@Test
	public void testDeclineChallengeInvalidUser() {
		// given
		UserEntity sender = new UserEntity();
		sender.setId(1L);
		sender.setLevel(Level.MASTER);
		sender.setLogin("white");
		sender.setPoints(1000);
		UserEntity receiver = new UserEntity();
		receiver.setId(2L);
		receiver.setLevel(Level.ADVANCED);
		receiver.setLogin("black");
		receiver.setPoints(2000);
		ChallengeEntity challenge = new ChallengeEntity();
		challenge.setId(1L);
		challenge.setSender(sender);
		challenge.setSenderLevel(Level.MASTER);
		challenge.setReceiver(receiver);
		challenge.setReceiverLevel(Level.ADVANCED);
		
		Mockito.when(userDao.get(sender.getId())).thenReturn(sender);
		Mockito.when(userDao.get(receiver.getId())).thenReturn(receiver);
		Mockito.when(challengeDao.get(challenge.getId())).thenReturn(challenge);
		
		// when
		boolean excpextedExceptionThrown = false;
		try {
			service.declineChallenge(challenge.getId(), sender.getId());
		} catch (EntityNotFoundException e) {
			excpextedExceptionThrown = false;
		} catch (InvalidUserException e) {
			excpextedExceptionThrown = true;
		}
		
		// then
		assertTrue(excpextedExceptionThrown);
	}
	
	@Test
	public void testFindUserByLogin() throws Exception {
		// given
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setLevel(Level.MASTER);
		userEntity.setLogin("user");
		userEntity.setName("name");
		userEntity.setPoints(1000);
		userEntity.setSurname("surname");
		
		Mockito.when(userDao.findUsersByLogin("user")).thenReturn(userEntity);
		
		// when
		UserStatsTO userTO = service.findChallengedPlayer("user");
		
		// then
		Mockito.verify(userDao).findUsersByLogin("user");
		
		assertNotNull(userTO);
		assertEquals(userEntity.getId(), userTO.getId());
		assertEquals(userEntity.getLogin(), userTO.getLogin());
		assertEquals(userEntity.getName(), userTO.getName());
		assertEquals(userEntity.getSurname(), userTO.getSurname());
	}
}
