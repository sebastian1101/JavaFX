package com.capgemini.chess.service.exceptions;

/**
 * Exception thrown in case entity was not found.
 * 
 * @author Michal Bejm
 *
 */
public class EntityNotFoundException extends Exception {
	
	private static final long serialVersionUID = -5177075707208391860L;

	public EntityNotFoundException() {
		super("Entity not found!");
	}
}
