package com.bnd.chemistry.business;

public class AcSpeciesIndecesToStoreGroup {

	private final int[] speciesIndeces;
	private final int speciesIndecesNum;
	private final int[] convertedSpeciesIndeces;

	public AcSpeciesIndecesToStoreGroup(int[] speciesIndeces, int[] convertedSpeciesIndeces) {
		this.speciesIndeces = speciesIndeces;
		this.speciesIndecesNum = speciesIndeces.length;
		this.convertedSpeciesIndeces = convertedSpeciesIndeces;
	}

	public int[] getSpeciesIndeces() {
		return speciesIndeces;
	}

	public int getSpeciesIndecesNum() {
		return speciesIndecesNum;
	}

	public int[] getConvertedSpeciesIndeces() {
		return convertedSpeciesIndeces;
	}
}
