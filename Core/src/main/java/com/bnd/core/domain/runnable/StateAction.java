package com.bnd.core.domain.runnable;

import java.io.Serializable;

import com.bnd.core.dynamics.StateAlternationType;
import com.bnd.core.util.ObjectUtil;

public class StateAction implements Comparable<StateAction>, Serializable {

	private Long id;
	private Long version = new Long(1);

	private Integer startTime;
	private Double timeLength;
	private StateAlternationType alternationType;

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

	public Integer getStartTime() {
		return startTime;
	}

	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}

	public Double getTimeLength() {
		return timeLength;
	}

	public void setTimeLength(Double timeLength) {
		this.timeLength = timeLength;
	}

	public StateAlternationType getAlternationType() {
		return alternationType;
	}

	public void setAlternationType(StateAlternationType alternationType) {
		this.alternationType = alternationType;
	}

	@Override
	public int compareTo(StateAction action) {
		return ObjectUtil.compareObjects(startTime, action.getStartTime());
	}
}