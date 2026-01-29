package com.auth.exception;

public class OtpExpiredException extends RuntimeException{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

   public OtpExpiredException (String message) {
	   super(message);
   }
}
