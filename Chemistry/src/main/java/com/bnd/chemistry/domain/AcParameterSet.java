package com.bnd.chemistry.domain;

public class AcParameterSet extends AcVariableSet<AcParameter> {

	private AcSpeciesSet speciesSet;

	public AcSpeciesSet getSpeciesSet() {
		return speciesSet;
	}

	public void setSpeciesSet(AcSpeciesSet speciesSet) {
		this.speciesSet = speciesSet;
	}

	@Override
	public Integer getNextVarSequenceNum() {
		return speciesSet.getNextVarSequenceNum();
	}
}