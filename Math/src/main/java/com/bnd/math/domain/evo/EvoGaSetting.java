package com.bnd.math.domain.evo;

import java.util.Date;

import com.bnd.core.domain.DomainObject;
import com.bnd.core.domain.TechnicalDomainObject;

import com.bnd.core.domain.um.User;

/**
 * The domain object for genetic algorithm.
 */
public class EvoGaSetting extends TechnicalDomainObject {

	private String name;
	private Date createTime;
	private User createdBy;

	private Integer eliteNumber;
	private int populationSize;
	private boolean conditionalMutationFlag;
	private boolean conditionalCrossOverFlag;
	private Double crossOverProbability;
	private Double mutationProbability;
	private Double perBitMutationProbability;
	private Double pertrubMutationStrength;

	private CrossOverType crossOverType;
	private MutationType mutationType;
	private BitMutationType bitMutationType;
	private SelectionType selectionType;

	private FitnessRenormalizationType fitnessRenormalizationType;
	private Integer generationLimit;
	private boolean maxValueFlag;

	/**
	 * Gets the value of the attribute name back.
	 * 
	 * @return The value of the attribute name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the attribute name.
	 * 
	 * @param name The value to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the value of the attribute generationLimit.
	 * 
	 * @return The value of the attribute generationLimit.
	 */
	public Integer getGenerationLimit() {
		return generationLimit;
	}

	/**
	 * Sets the value of the attribute generationLimit.
	 *
	 * @param generationLimit The value to set.
	 */
	public void setGenerationLimit(Integer generationLimit) {
		this.generationLimit = generationLimit;
	}

	/**
	 * Gets the value of the attribute conditionalCrossOverFlag back.
	 * 
	 * @return The value of the attribute conditionalCrossOverFlag.
	 */
	public boolean isConditionalCrossOverFlag() {
		return conditionalCrossOverFlag;
	}

	/**
	 * Sets the value of the attribute conditionalCrossOverFlag.
	 *
	 * @param conditionalCrossOverFlag The value to set.
	 */
	public void setConditionalCrossOverFlag(boolean conditionalMutationFlag) {
		this.conditionalCrossOverFlag = conditionalMutationFlag;
	}

	/**
	 * Gets the value of the attribute conditionalMutationFlag back.
	 * 
	 * @return The value of the attribute conditionalMutationFlag.
	 */
	public boolean isConditionalMutationFlag() {
		return conditionalMutationFlag;
	}

	/**
	 * Sets the value of the attribute conditionalMutationFlag.
	 *
	 * @param conditionalMutationFlag The value to set.
	 */
	public void setConditionalMutationFlag(boolean conditionalMutationFlag) {
		this.conditionalMutationFlag = conditionalMutationFlag;
	}

	/**
	 * Gets the value of the attribute crossOverProbability back.
	 * 
	 * @return The value of the attribute crossOverProbability.
	 */
	public Double getCrossOverProbability() {
		return crossOverProbability;
	}

	/**
	 * Sets the value of the attribute crossOverProbability.
	 *
	 * @param crossOverProbability The value to set.
	 */
	public void setCrossOverProbability(Double crossOverProbability) {
		this.crossOverProbability = crossOverProbability;
	}

	/**
	 * Gets the value of the attribute mutationProbability back.
	 * 
	 * @return The value of the attribute mutationProbability.
	 */
	public Double getMutationProbability() {
		return mutationProbability;
	}

	/**
	 * Sets the value of the attribute mutationProbability.
	 *
	 * @param mutationProbability The value to set.
	 */
	public void setMutationProbability(Double mutationProbability) {
		this.mutationProbability = mutationProbability;
	}

	/**
	 * Gets the value of the attribute perBitMutationProbability back.
	 * 
	 * @return The value of the attribute perBitMutationProbability.
	 */
	public Double getPerBitMutationProbability() {
		return perBitMutationProbability;
	}

	/**
	 * Sets the value of the attribute perBitMutationProbability.
	 *
	 * @param perBitMutationProbability The value to set.
	 */
	public void setPerBitMutationProbability(Double perBitMutationProbability) {
		this.perBitMutationProbability = perBitMutationProbability;
	}

	/**
	 * Gets the value of the attribute mutationType back.
	 * 
	 * @return The value of the attribute mutationType.
	 */
	public MutationType getMutationType() {
		return mutationType;
	}

	/**
	 * Sets the value of the attribute mutationType.
	 *
	 * @param mutationType The value to set.
	 */
	public void setMutationType(MutationType mutationType) {
		this.mutationType = mutationType;
	}

	/**
	 * Gets the value of the attribute fitnessRenormalizationType back.
	 * 
	 * @return The value of the attribute fitnessRenormalizationType.
	 */
	public FitnessRenormalizationType getFitnessRenormalizationType() {
		return fitnessRenormalizationType;
	}

	/**
	 * Sets the value of the attribute fitnessRenormalizationType.
	 *
	 * @param fitnessRenormalizationType The value to set.
	 */
	public void setFitnessRenormalizationType(FitnessRenormalizationType fitnessRenormalizationType) {
		this.fitnessRenormalizationType = fitnessRenormalizationType;
	}

	/**
	 * Gets the value of the attribute eliteNumber back.
	 * 
	 * @return The value of the attribute eliteNumber.
	 */
	public Integer getEliteNumber() {
		return eliteNumber;
	}

	/**
	 * Sets the value of the attribute eliteNumber.
	 *
	 * @param eliteNumber The value to set.
	 */
	public void setEliteNumber(Integer eliteNumber) {
		this.eliteNumber = eliteNumber;
	}

	/**
	 * Gets the value of the attribute selectionType back.
	 * 
	 * @return The value of the attribute selectionType.
	 */
	public SelectionType getSelectionType() {
		if (selectionType == null) {
			selectionType = eliteNumber != null ? SelectionType.Elite : SelectionType.Roulette;
		}
		return selectionType;
	}

	/**
	 * Sets the value of the attribute selectionType.
	 *
	 * @param selectionType The value to set.
	 */
	public void setSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

	/**
	 * Gets the value of the attribute maxValueFlag.
	 * 
	 * @return The value of the attribute maxValueFlag.
	 */
	public boolean isMaxValueFlag() {
		return maxValueFlag;
	}

	/**
	 * Sets the value of the attribute maxValueFlag.
	 *
	 * @param maxValueFlag The value to set.
	 */
	public void setMaxValueFlag(boolean maxValueFlag) {
		this.maxValueFlag = maxValueFlag;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Double getPertrubMutationStrength() {
		return pertrubMutationStrength;
	}

	public void setPertrubMutationStrength(Double pertrubMutationStrength) {
		this.pertrubMutationStrength = pertrubMutationStrength;
	}

	public CrossOverType getCrossOverType() {
		return crossOverType;
	}

	public void setCrossOverType(CrossOverType crossOverType) {
		this.crossOverType = crossOverType;
	}

	public BitMutationType getBitMutationType() {
		return bitMutationType;
	}

	public void setBitMutationType(BitMutationType bitMutationType) {
		this.bitMutationType = bitMutationType;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer theSB = new StringBuffer();
		theSB.append(name);
		return theSB.toString();
	}

	/**
	 * @see DomainObject#copyFrom(DomainObject)
	 */
	@Override
	public void copyFrom(DomainObject<Long> domainObject) {
		if (domainObject == null || !(domainObject instanceof EvoGaSetting)) {
			return;
		}
		EvoGaSetting gaDO = (EvoGaSetting) domainObject;
		super.copyFrom(gaDO);
		setName(gaDO.getName());
		setEliteNumber(gaDO.getEliteNumber());
		setConditionalCrossOverFlag(gaDO.isConditionalCrossOverFlag());
		setConditionalMutationFlag(gaDO.isConditionalMutationFlag());
		setMutationType(gaDO.getMutationType());
		setFitnessRenormalizationType(gaDO.getFitnessRenormalizationType());
		setCrossOverProbability(gaDO.getCrossOverProbability());
		setMutationProbability(gaDO.getMutationProbability());
		setPerBitMutationProbability(gaDO.getPerBitMutationProbability());
		setSelectionType(gaDO.getSelectionType());
		setGenerationLimit(gaDO.getGenerationLimit());
	}
}