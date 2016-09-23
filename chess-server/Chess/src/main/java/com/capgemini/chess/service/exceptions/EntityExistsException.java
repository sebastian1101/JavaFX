package com.capgemini.chess.service.exceptions;

/**
 * Exception thrown in case entity already exists.
 * 
 * @author Michal Bejm
 *
 */
public class EntityExistsException extends Exception {
	
	private static final long serialVersionUID = -1426502905703141490L;

	public EntityExistsException() {
		super("Entity already exists!");
	}
}
