package com.bnd.function.domain;

import java.util.HashMap;
import java.util.Map;

import com.bnd.function.BndFunctionException;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public enum OperationType {
	Add("+", false),
	Sub("-", false),
	Mul("*", false),
	Div("/", false),
	Pow("^", false),
	Gr(">", false),
	Lt("<", false),
	Eq("=", false),
	Sgn("sgn", true),
	Abs("abs", true),
	Sq("sq", true),
	Sqrt("sqrt", true),
	Min("min", true),
	Max("max", true),
	Rand("rand", true),
	Sin("sin", true),
	Cos("cos", true);

	private static Map<String, OperationType> REPRESENTATION_OPERATION_MAP = new HashMap<String, OperationType>();
	static {
		for (OperationType operationType : OperationType.values()) {
			REPRESENTATION_OPERATION_MAP.put(operationType.getRepresentation(), operationType);
		}
	}

	private String representation;
	private boolean functionFlag;

	private OperationType(final String representation, boolean functionFlag) {
		this.representation = representation;
		this.functionFlag = functionFlag;
	}

	public String getRepresentation() {
		return representation;
	}

	public static OperationType fromRepresentation(String repr) {
		OperationType opType = REPRESENTATION_OPERATION_MAP.get(repr);
		if (opType == null) {
			throw new BndFunctionException("Operation '" + repr + "' not recognized.");
		}
		return REPRESENTATION_OPERATION_MAP.get(repr);
	}

	public boolean isFunction() {
		return functionFlag;
	}

	@Override
	public String toString() {
		return representation;
	}
}