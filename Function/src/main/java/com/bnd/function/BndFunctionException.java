package com.bnd.function;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class BndFunctionException extends RuntimeException {

	public BndFunctionException() {
		super();
	}

	public BndFunctionException(String message) {
		super(message);
	}

	public BndFunctionException(String message, Throwable cause) {
		super(message, cause);
	}
}
