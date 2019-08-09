package com.bnd.core.exec;

public class ExternalProcessFailedException extends RuntimeException {

	public ExternalProcessFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
