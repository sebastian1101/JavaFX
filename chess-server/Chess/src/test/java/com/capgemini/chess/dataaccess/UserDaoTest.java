package com.capgemini.chess.dataaccess;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.dao.impl.UserDaoImpl;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.Level;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class UserDaoTest {
	
	@Autowired
	UserDao userDao;
	
	@Configuration
	@EnableAutoConfiguration
	@EntityScan(basePackages = {"com.capgemini.chess.dataaccess"})
	static class UserDaoTestConfiguration {
		
		@Bean
		public UserDao userDao() {
			return new UserDaoImpl();
		}
		
		@Bean
	    public DataSource dataSource() throws SQLException {
	        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
	        return builder.setType(EmbeddedDatabaseType.H2).build();
	    }
	}
		

	@Before
	@Transactional
	public void initializeDB() {
    	UserEntity user1 = new UserEntity();
    	user1.setLevel(Level.EXPERIENCED_MIDDLEBORW);
    	user1.setPoints(600);
    	user1.setLogin("user1");
    	user1.setName("name1");
    	user1.setSurname("surname1");
    	user1.setPassword("pass1");
    	userDao.create(user1);
    	
    	UserEntity user2 = new UserEntity();
    	user2.setLevel(Level.MASTER);
    	user2.setPoints(6000);
    	user2.setLogin("user2");
    	user2.setName("name2");
    	user2.setSurname("surname2");
    	user2.setPassword("pass2");
    	userDao.create(user2);
    	
    	UserEntity user3 = new UserEntity();
    	user3.setLevel(Level.NEWBIE);
    	user3.setPoints(0);
    	user3.setLogin("user3");
    	user3.setName("name3");
    	user3.setSurname("surname3");
    	user3.setPassword("pass3");
    	userDao.create(user3);
    }
	
	@Test
	@Transactional
	public void testFindUsersByLogin() {
		// given
		String login = "user1";
		
		//when
		UserEntity user = userDao.findUsersByLogin(login);
		
		// then
		assertNotNull(user);
		assertEquals(login, user.getLogin());
	}

	@Test
	@Transactional
	public void testFindUsersByLevels() {
		// given
		EnumSet<Level> levels = EnumSet.of(Level.MASTER, Level.NEWBIE);
		
		//when
		List<UserEntity> users = userDao.findUsersByLevels(levels, 0L);
		
		// then
		assertNotNull(users);
		assertEquals(2, users.size());
	}
	
	@Test
	@Transactional
	public void testFindTopUsers() {
		// given
		int first = 0;
		int limit = 2;
		
		//when
		List<UserEntity> users = userDao.findTopUsers(first, limit);
		
		// then
		assertNotNull(users);
		assertEquals(2, users.size());
		assertEquals("user2", users.get(0).getLogin());
		assertEquals("user1", users.get(1).getLogin());
	}
	
	@Test
	@Transactional
	public void testFindPlayersPostionInRank() {
		// given
		Level level = Level.MASTER;
		int points = 500;
		
		//when
		int position = userDao.findPlayersPostionInRank(level, points, 0L);
		
		// then
		assertEquals(2, position);
	}
	
	@Test
	@Transactional
	public void testFindUsers() {
		// given
		String login = "user1";
		
		//when
		List<UserEntity> users = userDao.findUsers(login, null, null);
		
		// then
		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals(login, users.get(0).getLogin());
	}
}
