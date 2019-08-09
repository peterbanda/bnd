package com.bnd.core.util;

import com.bnd.core.BndRuntimeException;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class BndConversionException extends BndRuntimeException {

	public BndConversionException(String message) {
		super(message);
	}

	public BndConversionException(String message, Throwable cause) {
		super(message, cause);
	}
}
