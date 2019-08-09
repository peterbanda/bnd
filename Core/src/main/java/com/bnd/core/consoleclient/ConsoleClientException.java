package com.bnd.core.consoleclient;

/**
 * ConsoleClientException is the Runtime exception of the console client.
 * It wraps message and possibly another exception that represents the original cause of error.
 * 
 * @author © Peter Banda
 * @since 2011  
 */
public class ConsoleClientException extends RuntimeException {

	public ConsoleClientException(String message) {
		super(message);
	}

	public ConsoleClientException(String message, Throwable cause) {
		super(message, cause);
	}
}
