package com.bnd.chemistry.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.bnd.core.domain.um.User;
import com.bnd.math.domain.rand.RandomDistribution;

public class ArtificialChemistrySpec implements Serializable {

	private Long id;
	private Long version = 1l;
	private Date timeCreated = new Date();
	private User createdBy;
	private String name;

	private RandomDistribution<Double> rateConstantDistribution;
	private boolean includeReverseReactions;
	private AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy;

	// Influx
	private Double influxRatio;
	private RandomDistribution<Double> influxRateConstantDistribution;

	// Outflux
	private Double outfluxRatio;
	private boolean outfluxAll;
	private RandomDistribution<Double> outfluxRateConstantDistribution;
	private RandomDistribution<Double> outfluxNonReactiveRateConstantDistribution;

	private Double constantSpeciesRatio;

	private Collection<ArtificialChemistry> acs = new ArrayList<ArtificialChemistry>();

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

	public RandomDistribution<Double> getRateConstantDistribution() {
		return rateConstantDistribution;
	}

	public void setRateConstantDistribution(RandomDistribution<Double> rateConstantDistribution) {
		this.rateConstantDistribution = rateConstantDistribution;
	}

	public boolean isIncludeReverseReactions() {
		return includeReverseReactions;
	}

	public void setIncludeReverseReactions(boolean includeReverseReactions) {
		this.includeReverseReactions = includeReverseReactions;
	}

	public AcReactionSpeciesForbiddenRedundancy getSpeciesForbiddenRedundancy() {
		return speciesForbiddenRedundancy;
	}

	public void setSpeciesForbiddenRedundancy(AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy) {
		this.speciesForbiddenRedundancy = speciesForbiddenRedundancy;
	}

	public RandomDistribution<Double> getInfluxRateConstantDistribution() {
		return influxRateConstantDistribution;
	}

	public void setInfluxRateConstantDistribution(RandomDistribution<Double> influxRateConstantDistribution) {
		this.influxRateConstantDistribution = influxRateConstantDistribution;
	}

	public RandomDistribution<Double> getOutfluxRateConstantDistribution() {
		return outfluxRateConstantDistribution;
	}

	public void setOutfluxRateConstantDistribution(RandomDistribution<Double> outfluxRateConstantDistribution) {
		this.outfluxRateConstantDistribution = outfluxRateConstantDistribution;
	}
	
	public boolean isOutfluxAll() {
		return outfluxAll;
	}

	public void setOutfluxAll(boolean outfluxAll) {
		this.outfluxAll = outfluxAll;
	}

	public Double getInfluxRatio() {
		return influxRatio;
	}

	public void setInfluxRatio(Double influxRatio) {
		this.influxRatio = influxRatio;
	}

	public Double getOutfluxRatio() {
		return outfluxRatio;
	}

	public void setOutfluxRatio(Double outfluxRatio) {
		this.outfluxRatio = outfluxRatio;
	}

	public Double getConstantSpeciesRatio() {
		return constantSpeciesRatio;
	}

	public void setConstantSpeciesRatio(Double constantSpeciesRatio) {
		this.constantSpeciesRatio = constantSpeciesRatio;
	}

	public Collection<ArtificialChemistry> getAcs() {
		return acs;
	}

	public void setAcs(Collection<ArtificialChemistry> acs) {
		this.acs = acs;
	}

	public void addAc(ArtificialChemistry ac) {
		acs.add(ac);
		ac.setGeneratedBySpec(this);
	}

	public void removeAc(ArtificialChemistry ac) {
		acs.remove(ac);
		ac.setGeneratedBySpec(null);
	}

	public boolean hasOutfluxNonReactiveRateConstantDistribution() {
		return outfluxNonReactiveRateConstantDistribution != null;
	}

	public RandomDistribution<Double> getOutfluxNonReactiveRateConstantDistribution() {
		return outfluxNonReactiveRateConstantDistribution;
	}

	public void setOutfluxNonReactiveRateConstantDistribution(RandomDistribution<Double> outfluxNonReactiveRateConstantDistribution) {
		this.outfluxNonReactiveRateConstantDistribution = outfluxNonReactiveRateConstantDistribution;
	}
}