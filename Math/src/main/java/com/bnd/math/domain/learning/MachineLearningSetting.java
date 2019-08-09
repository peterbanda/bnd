package com.bnd.math.domain.learning;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.dynamics.StateAlternationType;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class MachineLearningSetting extends TechnicalDomainObject {

	public enum LearningRateAnnealingType { Linear, Exponential }

	private Double singleIterationLength;
	private Integer iterationNum;
	private Double initialDelay;

	private StateAlternationType inputAlternationType;
	private Double inputTimeLength;

	private Double outputInterpretationRelativeTime;

	private Double initialLearningRate;
	private Double learningAnnealingRate;
	private LearningRateAnnealingType learningAnnealingType;
	private TrainingSampleSelectionType selectionType;

	public Double getInitialDelay() {
		return initialDelay;
	}

	public void setInitialDelay(Double initialDelay) {
		this.initialDelay = initialDelay;
	}
	
	public Double getInitialLearningRate() {
		return initialLearningRate;
	}

	public void setInitialLearningRate(Double initialLearningRate) {
		this.initialLearningRate = initialLearningRate;
	}

	public Double getLearningAnnealingRate() {
		return learningAnnealingRate;
	}

	public void setLearningAnnealingRate(Double learningAnnealingRate) {
		this.learningAnnealingRate = learningAnnealingRate;
	}

	public Double getSingleIterationLength() {
		return singleIterationLength;
	}

	public void setSingleIterationLength(Double singleIterationLength) {
		this.singleIterationLength = singleIterationLength;
	}

//	public Integer getIterationNum() {
//		return iterationNum;
//	}
//
//	public void setIterationNum(Integer iterationNum) {
//		this.iterationNum = iterationNum;
//	}

	public StateAlternationType getInputAlternationType() {
		return inputAlternationType;
	}

	public void setInputAlternationType(StateAlternationType inputAlternationType) {
		this.inputAlternationType = inputAlternationType;
	}

	public Double getInputTimeLength() {
		return inputTimeLength;
	}

	public void setInputTimeLength(Double inputTimeLength) {
		this.inputTimeLength = inputTimeLength;
	}

	public Double getOutputInterpretationRelativeTime() {
		return outputInterpretationRelativeTime;
	}

	public void setOutputInterpretationRelativeTime(Double outputInterpretationRelativeTime) {
		this.outputInterpretationRelativeTime = outputInterpretationRelativeTime;
	}

	public TrainingSampleSelectionType getSelectionType() {
		return selectionType;
	}

	public void setSelectionType(TrainingSampleSelectionType selectionType) {
		this.selectionType = selectionType;
	}

	public LearningRateAnnealingType getLearningAnnealingType() {
		return learningAnnealingType;
	}

	public void setLearningAnnealingType(LearningRateAnnealingType learningAnnealingType) {
		this.learningAnnealingType = learningAnnealingType;
	}
}