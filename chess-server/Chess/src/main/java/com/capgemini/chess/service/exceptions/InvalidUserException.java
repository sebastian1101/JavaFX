package com.capgemini.chess.service.exceptions;

/**
 * Exception thrown in case invalid user tries to perform some operation.
 * 
 * @author Michal Bejm
 *
 */
public class InvalidUserException extends Exception {

	private static final long serialVersionUID = 684742938460119225L;

	public InvalidUserException() {
		super("Invalid user!");
	}
}
