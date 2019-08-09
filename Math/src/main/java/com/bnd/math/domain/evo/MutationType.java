package com.bnd.math.domain.evo;

/**
 * Enumeration defining all mutation types.
 */
public enum MutationType {

	OneBit("one bit"),
	TwoBits("two bits"),
	PerBit("per bit"),
	Exchange("exchange");

	/**
	 * The text of the mutation.
	 */
	private final String text;

	/**
	 * The default constructor.
	 * 
	 * @param text The text of the Mutation.
	 */
	private MutationType(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}