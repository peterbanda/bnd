package com.bnd.chemistry.domain;

import java.io.Serializable;
import java.util.Date;

import com.bnd.core.domain.ValueBound;
import com.bnd.core.domain.um.User;

public class ArtificialChemistrySpecBound implements Serializable {

	private Long id;
	private Long version = 1l;
	private Date timeCreated = new Date();
	private User createdBy;
	private String name;

	private ValueBound<Double> influxRatio = new ValueBound<Double>();
	private ValueBound<Double> outfluxRatio = new ValueBound<Double>();
	private ValueBound<Double> constantSpeciesRatio = new ValueBound<Double>();

	private ValueBound<Double> rateConstantDistributionShape = new ValueBound<Double>();
	private ValueBound<Double> rateConstantDistributionLocation = new ValueBound<Double>();
	private ValueBound<Double> influxRateConstantDistributionShape = new ValueBound<Double>();
	private ValueBound<Double> influxRateConstantDistributionLocation = new ValueBound<Double>();
	private ValueBound<Double> outfluxRateConstantDistributionShape = new ValueBound<Double>();
	private ValueBound<Double> outfluxRateConstantDistributionLocation = new ValueBound<Double>();
	private ValueBound<Double> outfluxNonReactiveRateConstantDistributionShape = new ValueBound<Double>();
	private ValueBound<Double> outfluxNonReactiveRateConstantDistributionLocation = new ValueBound<Double>();

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

	public ValueBound<Double> getInfluxRatio() {
		return influxRatio;
	}

	public void setInfluxRatio(ValueBound<Double> influxRatio) {
		this.influxRatio = influxRatio;
	}

	public Double getInfluxRatioFrom() {
		return influxRatio.getFrom();
	}

	public Double getInfluxRatioTo() {
		return influxRatio.getTo();
	}

	public void setInfluxRatioFrom(Double value) {
		influxRatio.setFrom(value);
	}

	public void setInfluxRatioTo(Double value) {
		influxRatio.setTo(value);
	}

	public ValueBound<Double> getOutfluxRatio() {
		return outfluxRatio;
	}

	public void setOutfluxRatio(ValueBound<Double> outfluxRatio) {
		this.outfluxRatio = outfluxRatio;
	}

	public Double getOutfluxRatioFrom() {
		return outfluxRatio.getFrom();
	}

	public Double getOutfluxRatioTo() {
		return outfluxRatio.getTo();
	}

	public void setOutfluxRatioFrom(Double value) {
		outfluxRatio.setFrom(value);
	}

	public void setOutfluxRatioTo(Double value) {
		outfluxRatio.setTo(value);
	}

	public ValueBound<Double> getConstantSpeciesRatio() {
		return constantSpeciesRatio;
	}

	public void setConstantSpeciesRatio(ValueBound<Double> constantSpeciesRatio) {
		this.constantSpeciesRatio = constantSpeciesRatio;
	}

	public Double getConstantSpeciesRatioFrom() {
		return constantSpeciesRatio.getFrom();
	}

	public Double getConstantSpeciesRatioTo() {
		return constantSpeciesRatio.getTo();
	}

	public void setConstantSpeciesRatioFrom(Double value) {
		constantSpeciesRatio.setFrom(value);
	}

	public void setConstantSpeciesRatioTo(Double value) {
		constantSpeciesRatio.setTo(value);
	}

	public ValueBound<Double> getRateConstantDistributionShape() {
		return rateConstantDistributionShape;
	}

	public void setRateConstantDistributionShape(ValueBound<Double> rateConstantDistributionShape) {
		this.rateConstantDistributionShape = rateConstantDistributionShape;
	}

	public Double getRateConstantDistributionShapeFrom() {
		return rateConstantDistributionShape.getFrom();
	}

	public Double getRateConstantDistributionShapeTo() {
		return rateConstantDistributionShape.getTo();
	}

	public void setRateConstantDistributionShapeFrom(Double value) {
		rateConstantDistributionShape.setFrom(value);
	}

	public void setRateConstantDistributionShapeTo(Double value) {
		rateConstantDistributionShape.setTo(value);
	}

	public ValueBound<Double> getRateConstantDistributionLocation() {
		return rateConstantDistributionLocation;
	}

	public void setRateConstantDistributionLocation(ValueBound<Double> rateConstantDistributionLocation) {
		this.rateConstantDistributionLocation = rateConstantDistributionLocation;
	}

	public Double getRateConstantDistributionLocationFrom() {
		return rateConstantDistributionLocation.getFrom();
	}

	public Double getRateConstantDistributionLocationTo() {
		return rateConstantDistributionLocation.getTo();
	}

	public void setRateConstantDistributionLocationFrom(Double value) {
		rateConstantDistributionLocation.setFrom(value);
	}

	public void setRateConstantDistributionLocationTo(Double value) {
		rateConstantDistributionLocation.setTo(value);
	}

	public ValueBound<Double> getInfluxRateConstantDistributionShape() {
		return influxRateConstantDistributionShape;
	}

	public void setInfluxRateConstantDistributionShape(ValueBound<Double> influxRateConstantDistributionShape) {
		this.influxRateConstantDistributionShape = influxRateConstantDistributionShape;
	}

	public Double getInfluxRateConstantDistributionShapeFrom() {
		return influxRateConstantDistributionShape.getFrom();
	}

	public Double getInfluxRateConstantDistributionShapeTo() {
		return influxRateConstantDistributionShape.getTo();
	}

	public void setInfluxRateConstantDistributionShapeFrom(Double value) {
		influxRateConstantDistributionShape.setFrom(value);
	}

	public void setInfluxRateConstantDistributionShapeTo(Double value) {
		influxRateConstantDistributionShape.setTo(value);
	}

	public ValueBound<Double> getInfluxRateConstantDistributionLocation() {
		return influxRateConstantDistributionLocation;
	}

	public void setInfluxRateConstantDistributionLocation(ValueBound<Double> influxRateConstantDistributionLocation) {
		this.influxRateConstantDistributionLocation = influxRateConstantDistributionLocation;
	}

	public Double getInfluxRateConstantDistributionLocationFrom() {
		return influxRateConstantDistributionLocation.getFrom();
	}

	public Double getInfluxRateConstantDistributionLocationTo() {
		return influxRateConstantDistributionLocation.getTo();
	}

	public void setInfluxRateConstantDistributionLocationFrom(Double value) {
		influxRateConstantDistributionLocation.setFrom(value);
	}

	public void setInfluxRateConstantDistributionLocationTo(Double value) {
		influxRateConstantDistributionLocation.setTo(value);
	}

	public ValueBound<Double> getOutfluxRateConstantDistributionShape() {
		return outfluxRateConstantDistributionShape;
	}

	public void setOutfluxRateConstantDistributionShape(ValueBound<Double> outfluxRateConstantDistributionShape) {
		this.outfluxRateConstantDistributionShape = outfluxRateConstantDistributionShape;
	}

	public Double getOutfluxRateConstantDistributionShapeFrom() {
		return outfluxRateConstantDistributionShape.getFrom();
	}

	public Double getOutfluxRateConstantDistributionShapeTo() {
		return outfluxRateConstantDistributionShape.getTo();
	}

	public void setOutfluxRateConstantDistributionShapeFrom(Double value) {
		outfluxRateConstantDistributionShape.setFrom(value);
	}

	public void setOutfluxRateConstantDistributionShapeTo(Double value) {
		outfluxRateConstantDistributionShape.setTo(value);
	}
	
	public ValueBound<Double> getOutfluxRateConstantDistributionLocation() {
		return outfluxRateConstantDistributionLocation;
	}

	public void setOutfluxRateConstantDistributionLocation(ValueBound<Double> outfluxRateConstantDistributionLocation) {
		this.outfluxRateConstantDistributionLocation = outfluxRateConstantDistributionLocation;
	}

	public Double getOutfluxRateConstantDistributionLocationFrom() {
		return outfluxRateConstantDistributionLocation.getFrom();
	}

	public Double getOutfluxRateConstantDistributionLocationTo() {
		return outfluxRateConstantDistributionLocation.getTo();
	}

	public void setOutfluxRateConstantDistributionLocationFrom(Double value) {
		outfluxRateConstantDistributionLocation.setFrom(value);
	}

	public void setOutfluxRateConstantDistributionLocationTo(Double value) {
		outfluxRateConstantDistributionLocation.setTo(value);
	}

	public ValueBound<Double> getOutfluxNonReactiveRateConstantDistributionShape() {
		return outfluxNonReactiveRateConstantDistributionShape;
	}

	public void setOutfluxNonReactiveRateConstantDistributionShape(ValueBound<Double> outfluxNonReactiveRateConstantDistributionShape) {
		this.outfluxNonReactiveRateConstantDistributionShape = outfluxNonReactiveRateConstantDistributionShape;
	}

	public boolean hasOutfluxNonReactiveRateConstantDistributionShape() {
		return getOutfluxNonReactiveRateConstantDistributionLocationFrom() != null && getOutfluxNonReactiveRateConstantDistributionLocationTo() != null; 
	}

	public Double getOutfluxNonReactiveRateConstantDistributionShapeFrom() {
		return outfluxNonReactiveRateConstantDistributionShape.getFrom();
	}

	public Double getOutfluxNonReactiveRateConstantDistributionShapeTo() {
		return outfluxNonReactiveRateConstantDistributionShape.getTo();
	}

	public void setOutfluxNonReactiveRateConstantDistributionShapeFrom(Double value) {
		outfluxNonReactiveRateConstantDistributionShape.setFrom(value);
	}

	public void setOutfluxNonReactiveRateConstantDistributionShapeTo(Double value) {
		outfluxNonReactiveRateConstantDistributionShape.setTo(value);
	}
	
	public ValueBound<Double> getOutfluxNonReactiveRateConstantDistributionLocation() {
		return outfluxNonReactiveRateConstantDistributionLocation;
	}

	public void setOutfluxNonReactiveRateConstantDistributionLocation(ValueBound<Double> outfluxNonReactiveRateConstantDistributionLocation) {
		this.outfluxNonReactiveRateConstantDistributionLocation = outfluxNonReactiveRateConstantDistributionLocation;
	}

	public Double getOutfluxNonReactiveRateConstantDistributionLocationFrom() {
		return outfluxNonReactiveRateConstantDistributionLocation.getFrom();
	}

	public Double getOutfluxNonReactiveRateConstantDistributionLocationTo() {
		return outfluxNonReactiveRateConstantDistributionLocation.getTo();
	}

	public void setOutfluxNonReactiveRateConstantDistributionLocationFrom(Double value) {
		outfluxNonReactiveRateConstantDistributionLocation.setFrom(value);
	}

	public void setOutfluxNonReactiveRateConstantDistributionLocationTo(Double value) {
		outfluxNonReactiveRateConstantDistributionLocation.setTo(value);
	}
}