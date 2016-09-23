package com.capgemini.chess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.capgemini.chess.service.UserService;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.to.UserProfileTO;

/**
 * Implementation of {@link UserDetailsImpl}.
 * 
 * @author Michal Bejm
 *
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService service;
	
	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		try {
			UserProfileTO user = service.findUserProfileByLogin(login);
			return new UserDetailsImpl(user);
		} catch (EntityNotFoundException e) {
			throw new UsernameNotFoundException("no user found with login " + login);
		}
		
	}

}
