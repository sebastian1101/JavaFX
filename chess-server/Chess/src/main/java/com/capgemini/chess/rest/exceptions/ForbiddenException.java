package com.capgemini.chess.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for 403 - Forbidden http status.
 * 
 * @author Michal Bejm
 *
 */
@ResponseStatus(value= HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
	
	private static final long serialVersionUID = -2937945353203568444L;

    public ForbiddenException(Throwable cause) {
        super(cause);
    }
}
