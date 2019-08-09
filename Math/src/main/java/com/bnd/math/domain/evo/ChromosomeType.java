package com.bnd.math.domain.evo;

/**
 * The type of possible chromosome.
 */
public enum ChromosomeType {

	Standard("standard"), 
	Permutation("permutation"), 
	Network("graph");

	/**
	 * The textual representation.
	 */
	private String name;

	/**
	 * Creates new instance of the class ChromosomeType and initializes it.
	 *
	 * @param name
	 */
	ChromosomeType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
