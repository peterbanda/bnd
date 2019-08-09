package com.bnd.math.domain.evo;

/**
 * Enumeration cross over types.
 */
public enum CrossOverType {

	Split("split"),
	Shuffle("shuffle");

	/**
	 * The text of the mutation.
	 */
	private final String text;

	/**
	 * The default constructor.
	 * 
	 * @param text The text of the Mutation.
	 */
	private CrossOverType(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}