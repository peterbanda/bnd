package com.bnd.math.domain.dynamics;

import java.util.Date;

import com.bnd.core.domain.um.User;
import com.bnd.math.domain.rand.RandomDistribution;

public class MultiRunAnalysisSpec<T> {

	private Long id;
	private Long version = new Long(1);
	private Date timeCreated = new Date();
	private User createdBy;

	private String name;
	private SingleRunAnalysisSpec singleRunSpec;
	private RandomDistribution<T> initialStateDistribution;
	private Integer runNum;

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

	public SingleRunAnalysisSpec getSingleRunSpec() {
		return singleRunSpec;
	}

	public void setSingleRunSpec(SingleRunAnalysisSpec singleRunSpec) {
		this.singleRunSpec = singleRunSpec;
	}

	public RandomDistribution<T> getInitialStateDistribution() {
		return initialStateDistribution;
	}

	public void setInitialStateDistribution(RandomDistribution<T> initialStateDistribution) {
		this.initialStateDistribution = initialStateDistribution;
	}

	public Integer getRunNum() {
		return runNum;
	}

	public void setRunNum(Integer runNum) {
		this.runNum = runNum;
	}
}