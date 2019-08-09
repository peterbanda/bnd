package com.bnd.chemistry.business;

import com.bnd.chemistry.BndChemistryException;

public class BndChemistryValidationException extends BndChemistryException {

	public BndChemistryValidationException() {
		super();
	}

	public BndChemistryValidationException(String message) {
		super(message);
	}

	public BndChemistryValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
