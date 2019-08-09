package com.bnd.chemistry.domain;

import java.util.ArrayList;
import java.util.Collection;

public class AcDNAStrandSpeciesSet extends AcSpeciesSet {
	private Collection<AcSpecies> upperStrands = new ArrayList<AcSpecies>();
	private Collection<AcSpecies> lowerStrands = new ArrayList<AcSpecies>();
	private Collection<AcCompositeSpecies> fullDoubleStrands = new ArrayList<AcCompositeSpecies>();
	private Collection<AcCompositeSpecies> partialDoubleStrands = new ArrayList<AcCompositeSpecies>();

	public Collection<AcSpecies> getUpperStrands() {
		return upperStrands;
	}

	public void setUpperStrands(Collection<AcSpecies> upperStrands) {
		this.upperStrands = upperStrands;
	}

	public Collection<AcSpecies> getLowerStrands() {
		return lowerStrands;
	}

	public void setLowerStrands(Collection<AcSpecies> lowerStrands) {
		this.lowerStrands = lowerStrands;
	}

	public Collection<AcCompositeSpecies> getFullDoubleStrands() {
		return fullDoubleStrands;
	}

	public void setFullDoubleStrands(Collection<AcCompositeSpecies> fullDoubleStrands) {
		this.fullDoubleStrands = fullDoubleStrands;
	}

	public Collection<AcCompositeSpecies> getPartialDoubleStrands() {
		return partialDoubleStrands;
	}

	public void setPartialDoubleStrands(Collection<AcCompositeSpecies> partialDoubleStrands) {
		this.partialDoubleStrands = partialDoubleStrands;
	}

	public void addUpperStrand(AcSpecies upperStrand) {
		upperStrands.add(upperStrand);
		addVariable(upperStrand);
	}

	public void addLowerStrand(AcSpecies lowerStrand) {
		lowerStrands.add(lowerStrand);
		addVariable(lowerStrand);
	}

	public void addFullDoubleStrand(AcCompositeSpecies fullDoubleStrand) {
		fullDoubleStrands.add(fullDoubleStrand);
		addVariable(fullDoubleStrand);
	}

	public void addPartialDoubleStrand(AcCompositeSpecies partialDoubleStrand) {
		partialDoubleStrands.add(partialDoubleStrand);
		addVariable(partialDoubleStrand);
	}
}