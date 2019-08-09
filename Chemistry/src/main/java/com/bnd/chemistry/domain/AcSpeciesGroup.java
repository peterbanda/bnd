package com.bnd.chemistry.domain;

import java.util.Collection;

// TODO: do we need this?
public class AcSpeciesGroup extends AcVariableGroup<AcSpecies> {

	public AcSpeciesGroup(Collection<AcSpecies> species) {
		super(species);
	}

	public AcSpeciesGroup() {
		super();
	}

	public AcSpeciesGroup(String label) {
		super();
		setLabel(label);
	}

	@Override
	public void addVariable(AcSpecies variable) {
		super.addVariable(variable);
		variable.setGroup(this);
	}
}