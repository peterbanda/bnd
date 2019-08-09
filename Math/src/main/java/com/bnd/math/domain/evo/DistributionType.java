package com.bnd.math.domain.evo;

/**
 * Enumeration defining possible probability distribution content.
 */
public enum DistributionType {

	SinglePoint("Single point"), 
	Uniform("Uniform"),
	DensityUniform("Density uniform"), 
	Boss2dDensityUniform("Boss 2D Density uniform"), 
	SelectedDensityUniform("Selected density uniform"), 
	MiddleDensityUniform("Middle density uniform");
	
	/**
	 * The textual representation of the distribution.
	 */
	private String text;

	/**
	 * The default constructor.
	 * 
	 * @param text The content of the Fitness.
	 */
	private DistributionType(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}