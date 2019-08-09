package com.bnd.chemistry.domain;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.util.ObjectUtil;

public class AcEvaluatedSpeciesAction extends TechnicalDomainObject {

	private AcSpeciesInteraction speciesAction;
	private AcEvaluatedAction evaluatedAction;
	private Double value;

	public AcSpeciesInteraction getSpeciesAction() {
		return speciesAction;
	}

	public void setSpeciesAction(AcSpeciesInteraction speciesAction) {
		this.speciesAction = speciesAction;
	}

	public AcEvaluatedAction getEvaluatedAction() {
		return evaluatedAction;
	}

	protected void setEvaluatedAction(AcEvaluatedAction evaluatedAction) {
		this.evaluatedAction = evaluatedAction;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public AcSpecies getSpecies() {
		return speciesAction.getSpecies();
	}

	/**
	 * @see edu.tlab.rbnpg.domain.TechnicalDomainObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!super.equals(object)) {
			return false;
		}
		if (object == null || !(object instanceof AcEvaluatedSpeciesAction)) {
			return false;
		}
		AcEvaluatedSpeciesAction evaluatedSpeciesAction = (AcEvaluatedSpeciesAction) object;
		return ObjectUtil.areObjectsEqual(getSpecies(), evaluatedSpeciesAction.getSpecies());
	}
}