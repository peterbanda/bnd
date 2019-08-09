package com.bnd.function.domain;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public enum AggregateFunction {

	Min("min"), Max("max"), Avg("avg"), First("first"), Middle("middle"), Last("last");

	private final String functionTag;

	AggregateFunction(String functionTag) {
		this.functionTag = functionTag;
	}

	public String getFunctionTag() {
		return functionTag;
	}
}
