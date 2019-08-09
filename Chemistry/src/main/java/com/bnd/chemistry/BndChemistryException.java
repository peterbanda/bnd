package com.bnd.chemistry;

/**
 * @author © Peter Banda
 * @since 2013
 */
public class BndChemistryException extends RuntimeException {

	public BndChemistryException() {
		super();
	}

	public BndChemistryException(String message) {
		super(message);
	}

	public BndChemistryException(String message, Throwable cause) {
		super(message, cause);
	}
}
