package com.bnd.network;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class BndNetworkException extends RuntimeException {

	public BndNetworkException() {
		super();
	}

	public BndNetworkException(String message) {
		super(message);
	}

	public BndNetworkException(String message, Throwable cause) {
		super(message, cause);
	}
}
