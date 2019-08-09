package com.bnd.chemistry.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;

public class ArtificialChemistry extends TechnicalDomainObject {

	private String name;
	private Date createTime = new Date();
	private User createdBy;

	private AcCompartment skinCompartment;

	private ArtificialChemistrySpec generatedBySpec;
	private AcSimulationConfig simulationConfig;

	private Collection<AcMultiRunAnalysisResult> multiRunAnalysisResults = new ArrayList<AcMultiRunAnalysisResult>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public AcCompartment getSkinCompartment() {
		return skinCompartment;
	}

	public void setSkinCompartment(AcCompartment skinCompartment) {
		this.skinCompartment = skinCompartment;
	}

	public AcSpeciesSet getSpeciesSet() {
		if (skinCompartment != null) {
			return skinCompartment.getSpeciesSet();
		}
		return null;
	}

	public Collection<AcSpecies> getSpecies() {
		if (skinCompartment != null) {
			return skinCompartment.getSpecies();
		}
		return null;
	}

	public Collection<AcReaction> getReactions() {
		if (skinCompartment != null) {
			return skinCompartment.getReactionSet().getReactions();
		}
		return null;
	}

	public AcReactionSet getReactionSet() {
		if (skinCompartment != null) {
			return skinCompartment.getReactionSet();
		}
		return null;
	}

	public Collection<AcMultiRunAnalysisResult> getMultiRunAnalysisResults() {
		return multiRunAnalysisResults;
	}

	public void setMultiRunAnalysisResults(Collection<AcMultiRunAnalysisResult> multiRunAnalysisResults) {
		this.multiRunAnalysisResults = multiRunAnalysisResults;
	}

	public void addMultiRunAnalysisResult(AcMultiRunAnalysisResult multiRunAnalysisResult) {
		multiRunAnalysisResult.setAc(this);
		multiRunAnalysisResults.add(multiRunAnalysisResult);
	}

	public void removeMultiRunAnalysisResult(AcMultiRunAnalysisResult multiRunAnalysisResult) {
		multiRunAnalysisResult.setAc(null);
		multiRunAnalysisResults.remove(multiRunAnalysisResult);
	}

	public ArtificialChemistrySpec getGeneratedBySpec() {
		return generatedBySpec;
	}

	public void setGeneratedBySpec(ArtificialChemistrySpec generatedBySpec) {
		this.generatedBySpec = generatedBySpec;
	}

	public AcSimulationConfig getSimulationConfig() {
		return simulationConfig;
	}

	public void setSimulationConfig(AcSimulationConfig simulationConfig) {
		this.simulationConfig = simulationConfig;
	}

	public Double getTimeStep() {
		return simulationConfig.getTimeStep();
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if (name != null) {
    		sb.append(name);
    	} else {
    		sb.append("<no name>");
    	}
    	return sb.toString();
    }
}