package com.bnd.core.domain.task;

import java.io.Serializable;
import java.util.Date;

import com.bnd.core.domain.um.User;

public class Task implements Serializable {

	private Long id;
	private Long version = new Long(1);
	private Date timeCreated = new Date();
	private User createdBy;
	private boolean repeat;

	private boolean runOnGrid = true;
	private Integer jobsInSequenceNum = 1;
	private Integer maxJobsInParallelNum;

	public Task() {
		// no-op
	}

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

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean isRunOnGrid() {
		return runOnGrid;
	}

	public void setRunOnGrid(boolean runOnGrid) {
		this.runOnGrid = runOnGrid;
	}

	public Integer getJobsInSequenceNum() {
		return jobsInSequenceNum;
	}

	public void setJobsInSequenceNum(Integer jobsInSequenceNum) {
		this.jobsInSequenceNum = jobsInSequenceNum;
	}

	public Integer getMaxJobsInParallelNum() {
		return maxJobsInParallelNum;
	}

	public void setMaxJobsInParallelNum(Integer maxJobsInParallelNum) {
		this.maxJobsInParallelNum = maxJobsInParallelNum;
	}
}