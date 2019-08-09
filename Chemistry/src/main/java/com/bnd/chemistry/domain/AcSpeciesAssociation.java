package com.bnd.chemistry.domain;

import java.io.Serializable;

import com.bnd.core.util.ObjectUtil;

public class AcSpeciesAssociation implements Serializable {

	private AcSpecies species;
	private AcSpeciesAssociationType type;

	public AcSpeciesAssociation(AcSpecies species, AcSpeciesAssociationType type) {
		this.species = species;
		this.type = type;
	}

	public AcSpecies getSpecies() {
		return species;
	}

	public AcSpeciesAssociationType getType() {
		return type;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof AcSpeciesAssociation)) {
			return false;
		}
		AcSpeciesAssociation speciesAssociation = (AcSpeciesAssociation) object;
		return this == speciesAssociation
				|| (ObjectUtil.areObjectsNotNullAndEqual(species, speciesAssociation.getSpecies())
					&& ObjectUtil.areObjectsNotNullAndEqual(type, speciesAssociation.getType()));
	}

	@Override
	public int hashCode() {
		return ObjectUtil.getHashCode(new Object[] {species, type});	
	}
}