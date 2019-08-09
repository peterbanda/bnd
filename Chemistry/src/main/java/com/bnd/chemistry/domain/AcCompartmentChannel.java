package com.bnd.chemistry.domain;

import com.bnd.core.domain.TechnicalDomainObject;

public class AcCompartmentChannel extends TechnicalDomainObject {

	private AcCompartment compartment;
	private AcSpecies sourceSpecies;
	private AcChannelDirection direction;
	private Double permeability;
	private AcSpecies targetSpecies;

	public AcCompartment getCompartment() {
		return compartment;
	}

	public void setCompartment(AcCompartment compartment) {
		this.compartment = compartment;
	}

	public void setDirection(AcChannelDirection direction) {
		this.direction = direction;
	}

	public AcChannelDirection getDirection() {
		return direction;
	}

	public AcSpecies getSourceSpecies() {
		return sourceSpecies;
	}

	public void setSourceSpecies(AcSpecies sourceSpecies) {
		this.sourceSpecies = sourceSpecies;
	}

	public AcSpecies getTargetSpecies() {
		return targetSpecies;
	}

	public void setTargetSpecies(AcSpecies targetSpecies) {
		this.targetSpecies = targetSpecies;
	}

	public Double getPermeability() {
		return permeability;
	}

	public void setPermeability(Double permeability) {
		this.permeability = permeability;
	}
}