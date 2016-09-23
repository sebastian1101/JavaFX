package com.capgemini.chess.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for 409 - Conflict http status.
 * 
 * @author Michal Bejm
 *
 */
@ResponseStatus(value= HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
		
	private static final long serialVersionUID = 2753108342425169159L;

    public ConflictException(Throwable cause) {
        super(cause);
    }
}
