package com.capgemini.chess.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for 400 - Bad Request http status.
 * 
 * @author Michal Bejm
 *
 */
@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
	
	private static final long serialVersionUID = -8042718966873032103L;

    public BadRequestException(Throwable cause) {
        super(cause);
    }
}
