package com.auth.exception;

public class ResetNotAllowedException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResetNotAllowedException(String message) {
        super(message);
    }
}
