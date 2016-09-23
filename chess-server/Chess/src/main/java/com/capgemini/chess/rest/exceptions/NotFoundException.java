package com.capgemini.chess.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for 404 - Not found http status.
 * 
 * @author Michal Bejm
 *
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
