package com.bnd.chemistry.domain;

import java.io.Serializable;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.math.domain.rand.RandomDistribution;

public class AcSpeciesToReactionConstraints implements Serializable {

	private Integer fixedReactantAssocsNum;
	private Integer fixedProductAssocsNum;
	private Integer fixedCatalystAssocsNum;
	private Integer fixedInhibitorAssocsNum;
	private RandomDistribution<Double> randomReactantAssocsNumDistribution;
	private RandomDistribution<Double> randomProductAssocsNumDistribution;
	private RandomDistribution<Double> randomCatalystAssocsNumDistribution;
	private RandomDistribution<Double> randomInhibitorAssocsNumDistribution;
	private Integer reactantMultiplicity;

	public Integer getFixedReactantAssocsNum() {
		return fixedReactantAssocsNum;
	}

	public void setFixedReactantAssocsNum(Integer fixedReactantAssocsNum) {
		this.fixedReactantAssocsNum = fixedReactantAssocsNum;
	}

	public Integer getFixedProductAssocsNum() {
		return fixedProductAssocsNum;
	}

	public void setFixedProductAssocsNum(Integer fixedProductAssocsNum) {
		this.fixedProductAssocsNum = fixedProductAssocsNum;
	}

	public Integer getFixedCatalystAssocsNum() {
		return fixedCatalystAssocsNum;
	}

	public void setFixedCatalystAssocsNum(Integer fixedCatalystAssocsNum) {
		this.fixedCatalystAssocsNum = fixedCatalystAssocsNum;
	}

	public Integer getFixedInhibitorAssocsNum() {
		return fixedInhibitorAssocsNum;
	}

	public void setFixedInhibitorAssocsNum(Integer fixedInhibitorAssocsNum) {
		this.fixedInhibitorAssocsNum = fixedInhibitorAssocsNum;
	}

	public RandomDistribution<Double> getRandomReactantAssocsNumDistribution() {
		return randomReactantAssocsNumDistribution;
	}

	public void setRandomReactantAssocsNumDistribution(RandomDistribution<Double> randomReactantAssocsNumDistribution) {
		this.randomReactantAssocsNumDistribution = randomReactantAssocsNumDistribution;
	}

	public RandomDistribution<Double> getRandomProductAssocsNumDistribution() {
		return randomProductAssocsNumDistribution;
	}

	public void setRandomProductAssocsNumDistribution(RandomDistribution<Double> randomProductAssocsNumDistribution) {
		this.randomProductAssocsNumDistribution = randomProductAssocsNumDistribution;
	}

	public RandomDistribution<Double> getRandomCatalystAssocsNumDistribution() {
		return randomCatalystAssocsNumDistribution;
	}

	public void setRandomCatalystAssocsNumDistribution(RandomDistribution<Double> randomCatalystAssocsNumDistribution) {
		this.randomCatalystAssocsNumDistribution = randomCatalystAssocsNumDistribution;
	}

	public RandomDistribution<Double> getRandomInhibitorAssocsNumDistribution() {
		return randomInhibitorAssocsNumDistribution;
	}

	public void setRandomInhibitorAssocsNumDistribution(RandomDistribution<Double> randomInhibitorAssocsNumDistribution) {
		this.randomInhibitorAssocsNumDistribution = randomInhibitorAssocsNumDistribution;
	}

	public Integer getReactantMultiplicity() {
		return reactantMultiplicity;
	}

	public void setReactantMultiplicity(Integer reactantMultiplicity) {
		this.reactantMultiplicity = reactantMultiplicity;
	}

	public RandomDistribution<Double> getRandomAssocsNumDistribution(AcSpeciesAssociationType assocType) {
		switch (assocType) {
			case Reactant: return randomReactantAssocsNumDistribution;
			case Product: return randomProductAssocsNumDistribution;
			case Catalyst: return randomCatalystAssocsNumDistribution;
			case Inhibitor: return randomInhibitorAssocsNumDistribution;
			default: throw new BndChemistryException("Species association type '" + assocType + "' not recognized.");
		}
	}

	public Integer getFixedAssocsNum(AcSpeciesAssociationType assocType) {
		switch (assocType) {
			case Reactant: return fixedReactantAssocsNum;
			case Product: return fixedProductAssocsNum;
			case Catalyst: return fixedCatalystAssocsNum;
			case Inhibitor: return fixedInhibitorAssocsNum;
			default: throw new BndChemistryException("Species association type '" + assocType + "' not recognized.");
		}
	}

	// TODO: move to acUtil
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Fixed: ");
		for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
			sb.append(getFixedAssocsNum(assocType));
			sb.append(", ");
		}
		sb.append("\nRandom: ");
		for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
			sb.append(getRandomAssocsNumDistribution(assocType));
			sb.append(", ");
		}
		return sb.toString();
	}
}