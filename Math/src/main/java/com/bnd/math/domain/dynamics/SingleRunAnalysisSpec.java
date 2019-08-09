package com.bnd.math.domain.dynamics;

import java.util.Date;

import com.bnd.core.domain.um.User;

public class SingleRunAnalysisSpec {

	private Long id;
	private Long version = new Long(1);
	private Date timeCreated = new Date();
	private User createdBy;
	private String name;

	private Double timeStepLength;
	private Integer iterations;
	private Double lyapunovPerturbationStrength;
	private Double derridaPerturbationStrength;
	private Double derridaTimeLength;
	private Integer timeStepToFilter;
	private Double fixedPointDetectionPrecision;
	private Double derridaResolution;

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

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
	
	public Double getDerridaTimeLength() {
		return derridaTimeLength;
	}

	public void setDerridaTimeLength(Double derridaTimeLength) {
		this.derridaTimeLength = derridaTimeLength;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getTimeStepLength() {
		return timeStepLength;
	}

	public void setTimeStepLength(Double timeStepLength) {
		this.timeStepLength = timeStepLength;
	}

	public Integer getIterations() {
		return iterations;
	}

	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}

	public Double getLyapunovPerturbationStrength() {
		return lyapunovPerturbationStrength;
	}

	public void setLyapunovPerturbationStrength(Double lyapunovPerturbationStrength) {
		this.lyapunovPerturbationStrength = lyapunovPerturbationStrength;
	}

	public Double getDerridaPerturbationStrength() {
		return derridaPerturbationStrength;
	}

	public void setDerridaPerturbationStrength(Double derridaPerturbationStrength) {
		this.derridaPerturbationStrength = derridaPerturbationStrength;
	}

	public Integer getTimeStepToFilter() {
		return timeStepToFilter;
	}

	public void setTimeStepToFilter(Integer timeStepToFilter) {
		this.timeStepToFilter = timeStepToFilter;
	}

	public Double getFixedPointDetectionPrecision() {
		return fixedPointDetectionPrecision;
	}

	public void setFixedPointDetectionPrecision(Double fixedPointDetectionPrecision) {
		this.fixedPointDetectionPrecision = fixedPointDetectionPrecision;
	}

	public Double getDerridaResolution() {
		return derridaResolution;
	}

	public void setDerridaResolution(Double derridaResolution) {
		this.derridaResolution = derridaResolution;
	}
}