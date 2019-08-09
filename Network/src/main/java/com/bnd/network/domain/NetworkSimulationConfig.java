package com.bnd.network.domain;

import java.io.Serializable;
import java.util.Date;

public class NetworkSimulationConfig implements Serializable {

	private Long id;
	private Long version = 1l;
	private Date timeCreated = new Date();
	private String name;
	private Double fixedPointDetectionPrecision;
	private Double fixedPointDetectionPeriodicity;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getFixedPointDetectionPrecision() {
		return fixedPointDetectionPrecision;
	}

	public void setFixedPointDetectionPrecision(Double fixedPointDetectionPrecision) {
		this.fixedPointDetectionPrecision = fixedPointDetectionPrecision;
	}
	
	public Double getFixedPointDetectionPeriodicity() {
		return fixedPointDetectionPeriodicity;
	}

	public void setFixedPointDetectionPeriodicity(Double fixedPointDetectionPeriodicity) {
		this.fixedPointDetectionPeriodicity = fixedPointDetectionPeriodicity;
	}
}