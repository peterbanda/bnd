package com.bnd.core.util;

import com.bnd.core.BndRuntimeException;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class BndParseException extends BndRuntimeException {

	public BndParseException(String message) {
		super(message);
	}

	public BndParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
