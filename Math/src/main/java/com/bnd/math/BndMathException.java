package com.bnd.math;

/**
 * @author © Peter Banda
 * @since 2013
 */
public class BndMathException extends RuntimeException {

	public BndMathException() {
		super();
	}

	public BndMathException(String message) {
		super(message);
	}

	public BndMathException(String message, Throwable cause) {
		super(message, cause);
	}
}
