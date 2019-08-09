package com.bnd.function.domain;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class BooleanFunction extends AbstractFunction<Boolean, Boolean> {

	public static enum BooleanFunctionType {
		AND, OR, NOT, XOR, Parity
	}

	private BooleanFunctionType type;

	public BooleanFunction(BooleanFunctionType type) {
		this.type = type;
	}

	public BooleanFunctionType getType() {
		return type;
	}
}