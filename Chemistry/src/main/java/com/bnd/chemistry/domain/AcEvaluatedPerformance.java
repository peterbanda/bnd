package com.bnd.chemistry.domain;

import java.util.Date;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;

public class AcEvaluatedPerformance extends TechnicalDomainObject {

	private Date timeCreated = new Date();
	private User createdBy;

	private AcCompartment compartment;
	private AcSimulationConfig simulationConfig;
	private AcInteractionSeries actionSeries;
	private AcEvaluation evaluation;
	private Integer repetitions;	
	private Double[] averagedCorrectRates;
	private Integer length;

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public AcInteractionSeries getActionSeries() {
		return actionSeries;
	}

	public void setActionSeries(AcInteractionSeries actionSeries) {
		this.actionSeries = actionSeries;
	}

	public AcEvaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(AcEvaluation evaluation) {
		this.evaluation = evaluation;
	}

	public Integer getRepetitions() {
		return repetitions;
	}

	public void setRepetitions(Integer repetitions) {
		this.repetitions = repetitions;
	}
	
	public Double[] getAveragedCorrectRates() {
		return averagedCorrectRates;
	}

	public void setAveragedCorrectRates(Double[] averagedCorrectRates) {
		this.averagedCorrectRates = averagedCorrectRates;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public AcCompartment getCompartment() {
		return compartment;
	}

	public void setCompartment(AcCompartment compartment) {
		this.compartment = compartment;
	}

	public AcSimulationConfig getSimulationConfig() {
		return simulationConfig;
	}

	public void setSimulationConfig(AcSimulationConfig simulationConfig) {
		this.simulationConfig = simulationConfig;
	}
}