package com.capgemini.chess.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.chess.dataaccess.dao.UserDao;
import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.Level;
import com.capgemini.chess.service.UserService;
import com.capgemini.chess.service.exceptions.EntityExistsException;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.mapper.UserProfileMapper;
import com.capgemini.chess.service.to.UserProfileTO;

/**
 * Implementation of {@link UserService}.
 * 
 * @author Michal Bejm
 *
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao userDao;

	/** {@inheritDoc} */
	@Override
	public UserProfileTO updateUser(UserProfileTO userTo) {
		UserEntity userEntity = userDao.get(userTo.getId());
		userEntity = UserProfileMapper.update(userEntity, userTo);
		userEntity = userDao.update(userEntity);
		return UserProfileMapper.map(userEntity);
	}

	/** {@inheritDoc} */
	@Override
	public UserProfileTO createUser(UserProfileTO userTo) throws EntityExistsException {
		UserEntity user = userDao.findUsersByLogin(userTo.getLogin());
        if (user != null){
            throw new EntityExistsException();
        }
		
		UserEntity userEntity = UserProfileMapper.map(userTo);
		userEntity.setLevel(Level.NEWBIE);
		userEntity = userDao.create(userEntity);
		return UserProfileMapper.map(userEntity);
	}

	/** {@inheritDoc} */
	@Override
	public void deleteUser(Long id) {
		userDao.delete(id);
	}

	/** {@inheritDoc} */
	@Override
	public UserProfileTO findUserProfileByLogin(String login) throws EntityNotFoundException {
		UserEntity userEntity = userDao.findUsersByLogin(login);
		if (userEntity == null) {
			throw new EntityNotFoundException();
		}
		return UserProfileMapper.map(userEntity);
	}

	/** {@inheritDoc} */
	@Override
	public List<UserProfileTO> findUsers(String login, String name, String surname) {
		List<UserEntity> userEntities = userDao.findUsers(login, name, surname);
		return UserProfileMapper.map2TOs(userEntities);
	}

}
