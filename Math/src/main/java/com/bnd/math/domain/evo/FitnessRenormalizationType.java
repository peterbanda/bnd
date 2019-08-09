package com.bnd.math.domain.evo;

/**
 * Enumeration defining all fitness renormalization types.
 */
public enum FitnessRenormalizationType {

	StrictOrderIncreasing("strict order increasing"),
	GroupOrderIncreasing("group order increasing"),
	GroupJumpOrderIncreasing("group jump order increasing");

	/**
	 * The type of the fitness renormalization.
	 */
	private final String text;

	/**
	 * The default constructor.
	 * 
	 * @param text The content of the fitness.
	 */
	private FitnessRenormalizationType(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}