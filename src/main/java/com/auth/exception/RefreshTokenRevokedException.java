package com.auth.exception;

public class RefreshTokenRevokedException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RefreshTokenRevokedException(String message) {
        super(message);
    }
}
