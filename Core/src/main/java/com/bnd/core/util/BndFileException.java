package com.bnd.core.util;

import com.bnd.core.BndRuntimeException;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class BndFileException extends BndRuntimeException {

	public BndFileException(String message) {
		super(message);
	}

	public BndFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
