package com.bnd.chemistry.domain;

import java.io.Serializable;
import java.util.Date;

import com.bnd.core.domain.um.User;
import com.bnd.function.domain.ODESolverType;

public class AcSimulationConfig implements Serializable {

	private Long id;
	private Long version = 1l;
	private Date timeCreated = new Date();
	private User createdBy;
	private String name;

	private ODESolverType odeSolverType;
	private Double upperThreshold;
	private Double lowerThreshold;
	private Double timeStep;
	private Double tolerance;
	private Double fixedPointDetectionPrecision;
	private Double fixedPointDetectionPeriodicity;

	@Deprecated
	private Double influxScale;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getUpperThreshold() {
		return upperThreshold;
	}

	public void setUpperThreshold(Double upperThreshold) {
		this.upperThreshold = upperThreshold;
	}

	public Double getFixedPointDetectionPrecision() {
		return fixedPointDetectionPrecision;
	}

	public void setFixedPointDetectionPrecision(Double fixedPointDetectionPrecision) {
		this.fixedPointDetectionPrecision = fixedPointDetectionPrecision;
	}

	public Double getLowerThreshold() {
		return lowerThreshold;
	}

	public void setLowerThreshold(Double lowerThreshold) {
		this.lowerThreshold = lowerThreshold;
	}

	public Double getTimeStep() {
		return timeStep;
	}

	public void setTimeStep(Double timeStep) {
		this.timeStep = timeStep;
	}

	public ODESolverType getOdeSolverType() {
		return odeSolverType;
	}

	public void setOdeSolverType(ODESolverType odeSolverType) {
		this.odeSolverType = odeSolverType;
	}

	public Double getTolerance() {
		return tolerance;
	}

	public void setTolerance(Double tolerance) {
		this.tolerance = tolerance;
	}

	public Double getFixedPointDetectionPeriodicity() {
		return fixedPointDetectionPeriodicity;
	}

	public void setFixedPointDetectionPeriodicity(Double fixedPointDetectionPeriodicity) {
		this.fixedPointDetectionPeriodicity = fixedPointDetectionPeriodicity;
	}

	public Double getInfluxScale() {
		return influxScale;
	}

	public void setInfluxScale(Double influxScale) {
		this.influxScale = influxScale;
	}
}