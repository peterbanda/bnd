package com.bnd.chemistry.domain;

import com.bnd.chemistry.BndChemistryException;

public enum AcCollectiveSpeciesReactionAssociationType {
	AND("AND (All)"), OR("OR (At Least One)");

	private final String text;

	private AcCollectiveSpeciesReactionAssociationType(String text) {
		this.text = text;
	}

	public String getName() {
		return name();
	}

	public String toString() {
		return text;
	}

	public String getOperator() {
		switch (this) {
			case AND: return " * ";
			case OR: return " + ";
			default: throw new BndChemistryException("Collective species reaction associationType type '" + this + "' not recognized.");
		}
	}

	public static void main(String args[]) {
		String name = AcCollectiveSpeciesReactionAssociationType.AND.name();
		System.out.print(name);
	}
}