package com.bnd.chemistry.domain;

public enum AcSpeciesAssociationType {
	Reactant, Product, Catalyst, Inhibitor;

	public boolean isReactantOrProduct() {
		return this == Reactant || this == Product;
	}
}