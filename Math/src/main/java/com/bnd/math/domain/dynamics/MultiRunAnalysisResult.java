package com.bnd.math.domain.dynamics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.bnd.core.domain.um.User;

public class MultiRunAnalysisResult<T> implements Serializable {

	private Long id;
	private Date timeCreated = new Date();
	private User createdBy;

	private MultiRunAnalysisSpec<T> spec;
	private Collection<SingleRunAnalysisResult> singleRunResults = new ArrayList<SingleRunAnalysisResult>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public MultiRunAnalysisSpec<T> getSpec() {
		return spec;
	}

	public void setSpec(MultiRunAnalysisSpec<T> spec) {
		this.spec = spec;
	}

	public Collection<SingleRunAnalysisResult> getSingleRunResults() {
		return singleRunResults;
	}

	public void addSingleRunResult(SingleRunAnalysisResult singleRunResult) {
		singleRunResults.add(singleRunResult);
		singleRunResult.setMultiRunResult(this);
	}

	public void setSingleRunResults(Collection<SingleRunAnalysisResult> singleRunResults) {
		this.singleRunResults = singleRunResults;
	}
}