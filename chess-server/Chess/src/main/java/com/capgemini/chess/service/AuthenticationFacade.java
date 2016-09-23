package com.capgemini.chess.service;

/**
 * Facade for retrieving of authentication data.
 * 
 * @author Michal Bejm
 *
 */
public interface AuthenticationFacade {
	
	/**
	 * Gets an id of the user that is currently logged in.
	 * 
	 * @return id of the user that is currently logged in.
	 */
	public long getUserId();
}
