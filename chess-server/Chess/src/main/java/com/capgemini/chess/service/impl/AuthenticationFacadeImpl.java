package com.capgemini.chess.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.capgemini.chess.UserDetailsImpl;
import com.capgemini.chess.service.AuthenticationFacade;

/**
 * Implementation of {@link AuthenticationFacade}.
 * 
 * @author Michal Bejm
 *
 */
@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {
	
	/** {@inheritDoc} */
	@Override
	public long getUserId() {
		UserDetailsImpl user = 
				(UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user.getId();
	}

}
