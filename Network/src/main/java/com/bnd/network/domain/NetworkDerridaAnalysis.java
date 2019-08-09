package com.bnd.network.domain;

import java.io.Serializable;
import java.util.Date;

import com.bnd.core.domain.um.User;
import com.bnd.math.domain.StatsSequence;

public class NetworkDerridaAnalysis<T> implements Serializable {

	private Long id;
	private Long version = new Long(1);
	private Date timeCreated = new Date();
	private User createdBy;

	private Network<T> network;
	private Integer repetitions;
	private Integer runTime;
	private StatsSequence result;

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

	public Network<T> getNetwork() {
		return network;
	}

	public void setNetwork(Network<T> network) {
		this.network = network;
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

	public Integer getRepetitions() {
		return repetitions;
	}

	public void setRepetitions(Integer repetitions) {
		this.repetitions = repetitions;
	}

	public Integer getRunTime() {
		return runTime;
	}

	public void setRunTime(Integer runTime) {
		this.runTime = runTime;
	}

	public StatsSequence getResult() {
		return result;
	}

	public void setResult(StatsSequence result) {
		this.result = result;
	}
}