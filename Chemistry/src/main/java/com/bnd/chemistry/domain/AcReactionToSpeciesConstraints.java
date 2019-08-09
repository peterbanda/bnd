package com.bnd.chemistry.domain;

import java.io.Serializable;

import com.bnd.chemistry.BndChemistryException;

public class AcReactionToSpeciesConstraints implements Serializable {

	private Integer maxReactantsNum;
	private Integer maxProductsNum;
	private Integer maxCatalystsNum;
	private Integer maxInhibitorsNum;
	private Integer minReactantsNum;
	private Integer minProductsNum;
	private Integer minCatalystsNum;
	private Integer minInhibitorsNum;

	public Integer getMaxReactantsNum() {
		return maxReactantsNum;
	}

	public void setMaxReactantsNum(Integer maxReactantsNum) {
		this.maxReactantsNum = maxReactantsNum;
	}

	public Integer getMaxProductsNum() {
		return maxProductsNum;
	}

	public void setMaxProductsNum(Integer maxProductsNum) {
		this.maxProductsNum = maxProductsNum;
	}

	public Integer getMaxCatalystsNum() {
		return maxCatalystsNum;
	}

	public void setMaxCatalystsNum(Integer maxCatalystsNum) {
		this.maxCatalystsNum = maxCatalystsNum;
	}

	public Integer getMaxInhibitorsNum() {
		return maxInhibitorsNum;
	}

	public void setMaxInhibitorsNum(Integer maxInhibitorsNum) {
		this.maxInhibitorsNum = maxInhibitorsNum;
	}

	public Integer getMinReactantsNum() {
		return minReactantsNum;
	}

	public void setMinReactantsNum(Integer minReactantsNum) {
		this.minReactantsNum = minReactantsNum;
	}

	public Integer getMinProductsNum() {
		return minProductsNum;
	}

	public void setMinProductsNum(Integer minProductsNum) {
		this.minProductsNum = minProductsNum;
	}

	public Integer getMinCatalystsNum() {
		return minCatalystsNum;
	}

	public void setMinCatalystsNum(Integer minCatalystsNum) {
		this.minCatalystsNum = minCatalystsNum;
	}

	public Integer getMinInhibitorsNum() {
		return minInhibitorsNum;
	}

	public void setMinInhibitorsNum(Integer minInhibitorsNum) {
		this.minInhibitorsNum = minInhibitorsNum;
	}

	public Integer getMaxSpeciesAssocsNum(AcSpeciesAssociationType assocType) {
		switch (assocType) {
			case Reactant: return maxReactantsNum;
			case Product: return maxProductsNum;
			case Catalyst: return maxCatalystsNum;
			case Inhibitor: return maxInhibitorsNum;
			default: throw new BndChemistryException("Species association type '" + assocType + "' not recognized.");
		}
	}

	public Integer getMinSpeciesAssocsNum(AcSpeciesAssociationType assocType) {
		switch (assocType) {
			case Reactant: return minReactantsNum;
			case Product: return minProductsNum;
			case Catalyst: return minCatalystsNum;
			case Inhibitor: return minInhibitorsNum;
			default: throw new BndChemistryException("Species association type '" + assocType + "' not recognized.");
		}
	}

	// TODO: move to acUtil
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Mins: ");
		for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
			sb.append(getMinSpeciesAssocsNum(assocType));
			sb.append(", ");
		}
		sb.append("\nMaxs: ");
		for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
			sb.append(getMaxSpeciesAssocsNum(assocType));
			sb.append(", ");
		}
		return sb.toString();
	}
}