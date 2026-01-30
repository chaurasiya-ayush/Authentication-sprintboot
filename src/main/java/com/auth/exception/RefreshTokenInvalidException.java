package com.auth.exception;

public class RefreshTokenInvalidException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RefreshTokenInvalidException(String message) {
        super(message);
    }
}