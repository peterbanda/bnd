package com.bnd.core;

/**
 * The general runtime exception of the bnd-core project.
 * 
 * @author © Peter Banda
 * @since 2012
 */
public class BndRuntimeException extends RuntimeException {

	public BndRuntimeException(String message) {
		super(message);
	}

	public BndRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
