package com.bnd.network.domain;

import java.io.Serializable;
import java.util.Date;

import com.bnd.core.domain.um.User;

public class AbstractNetworkPerformance<T> implements Serializable {

	private Long id;
	private Long version = new Long(1);
	private Date timeCreated = new Date();
	private User createdBy;

	private Network<T> network;
	private NetworkActionSeries<T> interactionSeries;
	private NetworkEvaluation evaluation;
	private Integer repetitions;
	private Integer runTime;

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

	public NetworkActionSeries<T> getInteractionSeries() {
		return interactionSeries;
	}

	public void setInteractionSeries(NetworkActionSeries<T> interactionSeries) {
		this.interactionSeries = interactionSeries;
	}

	public NetworkEvaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(NetworkEvaluation evaluation) {
		this.evaluation = evaluation;
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
}