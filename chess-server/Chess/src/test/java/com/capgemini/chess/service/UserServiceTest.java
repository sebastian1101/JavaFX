package com.capgemini.chess.service;

import static org.junit.Assert.*;

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

import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.Level;
import com.capgemini.chess.service.exceptions.EntityExistsException;
import com.capgemini.chess.service.impl.UserServiceImpl;
import com.capgemini.chess.service.mapper.UserProfileMapper;
import com.capgemini.chess.service.to.UserProfileTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class UserServiceTest {

	@Autowired
	UserService service;
	
	@Autowired
	UserDao userDao;
	
	@Configuration
	static class RankServiceTestContextConfiguration {
		@Bean
		public UserService userService() {
			return new UserServiceImpl();
		}
		
		@Bean
		public UserDao userDao() {
			return Mockito.mock(UserDao.class);
		}
	}
	
	@Before
    public void setup() {
        Mockito.reset(userDao);
    }
	
//	@Test
//	public void testReadUserSuccessful() throws Exception {
//		// given
//		UserEntity userEntity = new UserEntity();
//		userEntity.setAboutMe("about me");
//		userEntity.setEmail("user@user.com");
//		userEntity.setId(1L);
//		userEntity.setLevel(Level.MASTER);
//		userEntity.setLifeMotto("motto");
//		userEntity.setLogin("user");
//		userEntity.setName("name");
//		userEntity.setPassword("password");
//		userEntity.setPoints(1000);
//		userEntity.setSurname("surname");
//		
//		Mockito.when(userDao.get(1L)).thenReturn(userEntity);
//		
//		// when
//		UserProfileTO userTO = service.readUser(1L);
//		
//		// then
//		Mockito.verify(userDao).get(1L);
//		
//		assertNotNull(userTO);
//		assertEquals(userEntity.getAboutMe(), userTO.getAboutMe());
//		assertEquals(userEntity.getEmail(), userTO.getEmail());
//		assertEquals(userEntity.getId(), userTO.getId());
//		assertEquals(userEntity.getLogin(), userTO.getLogin());
//		assertEquals(userEntity.getName(), userTO.getName());
//		assertEquals(userEntity.getPassword(), userTO.getPassword());
//		assertEquals(userEntity.getSurname(), userTO.getSurname());
//	}
//	
//	@Test
//	public void testReadUserEntityNotFound() {
//		// given		
//		Mockito.when(userDao.get(1L)).thenReturn(null);
//		
//		// when
//		boolean excpextedExceptionThrown = false;
//		try {
//			service.readUser(1L);
//		} catch (EntityNotFoundException e) {
//			excpextedExceptionThrown = true;
//		}
//		
//		// then
//		Mockito.verify(userDao).get(1L);
//		assertTrue(excpextedExceptionThrown);
//	}
	
	@Test
	public void testUpdateUser() throws Exception {
		// given
		UserProfileTO userTO = new UserProfileTO();
		userTO.setAboutMe("about me");
		userTO.setEmail("user@user.com");
		userTO.setId(1L);
		userTO.setLifeMotto("motto");
		userTO.setLogin("user");
		userTO.setName("name");
		userTO.setPassword("password");
		userTO.setSurname("surname");
		
		
		Mockito.when(userDao.update(Matchers.any(UserEntity.class))).thenAnswer(new Answer<UserEntity>() {
			@Override
			public UserEntity answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (UserEntity) args[0];
			}
		});
		Mockito.when(userDao.get(userTO.getId())).thenReturn(UserProfileMapper.map(userTO));
		
		// when
		userTO = service.updateUser(userTO);
		
		// then
		Mockito.verify(userDao).update(Matchers.any(UserEntity.class));
		
		assertNotNull(userTO);
		assertEquals("about me", userTO.getAboutMe());
		assertEquals("user@user.com", userTO.getEmail());
		assertEquals(1L, userTO.getId());
		assertEquals("user", userTO.getLogin());
		assertEquals("name", userTO.getName());
		assertEquals("password", userTO.getPassword());
		assertEquals("surname", userTO.getSurname());
	}
	
	@Test
	public void testCreateUserSuccessful() throws Exception {
		// given
		UserProfileTO userTO = new UserProfileTO();
		userTO.setAboutMe("about me");
		userTO.setEmail("user@user.com");
		userTO.setId(1L);
		userTO.setLifeMotto("motto");
		userTO.setLogin("user");
		userTO.setName("name");
		userTO.setPassword("password");
		userTO.setSurname("surname");
		
		Mockito.when(userDao.create(Matchers.any(UserEntity.class))).thenAnswer(new Answer<UserEntity>() {
			@Override
			public UserEntity answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (UserEntity) args[0];
			}
		});
		
		// when
		userTO = service.createUser(userTO);
		
		// then
		Mockito.verify(userDao).create(Matchers.any(UserEntity.class));
		
		assertNotNull(userTO);
		assertEquals("about me", userTO.getAboutMe());
		assertEquals("user@user.com", userTO.getEmail());
		assertEquals(1L, userTO.getId());
		assertEquals("user", userTO.getLogin());
		assertEquals("name", userTO.getName());
		assertEquals("password", userTO.getPassword());
		assertEquals("surname", userTO.getSurname());
	}
	
	@Test
	public void testCreateUserEntityExists() {
		// given
		UserProfileTO userTO = new UserProfileTO();
		userTO.setAboutMe("about me");
		userTO.setEmail("user@user.com");
		userTO.setId(1L);
		userTO.setLifeMotto("motto");
		userTO.setLogin("user");
		userTO.setName("name");
		userTO.setPassword("password");
		userTO.setSurname("surname");
		
		Mockito.when(userDao.findUsersByLogin("user")).thenReturn(new UserEntity());
		
		// when
		boolean excpextedExceptionThrown = false;
		try {
			service.createUser(userTO);
		} catch (EntityExistsException e) {
			excpextedExceptionThrown = true;
		}
		
		// then
		assertTrue(excpextedExceptionThrown);
	}
	
	@Test
	public void testDeleteUser() {
		// given
		long id = 1L;
		
		// when
		service.deleteUser(id);
		
		// then
		Mockito.verify(userDao).delete(id);
	}
	
	@Test
	public void testFindUserByLogin() throws Exception {
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
		
		Mockito.when(userDao.findUsersByLogin("user")).thenReturn(userEntity);
		
		// when
		UserProfileTO userTO = service.findUserProfileByLogin("user");
		
		// then
		Mockito.verify(userDao).findUsersByLogin("user");
		
		assertNotNull(userTO);
		assertEquals(userEntity.getAboutMe(), userTO.getAboutMe());
		assertEquals(userEntity.getEmail(), userTO.getEmail());
		assertEquals(userEntity.getId(), userTO.getId());
		assertEquals(userEntity.getLogin(), userTO.getLogin());
		assertEquals(userEntity.getName(), userTO.getName());
		assertEquals(userEntity.getPassword(), userTO.getPassword());
		assertEquals(userEntity.getSurname(), userTO.getSurname());
	}
}
