package com.bnd.math.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class StatsSequence {

	private Long id;
	private Date timeCreated = new Date();
	private Collection<Stats> stats = new ArrayList<Stats>();

	public StatsSequence() {
		timeCreated = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public Collection<Stats> getStats() {
		return stats;
	}

	public void setStats(Collection<Stats> stats) {
		this.stats = stats;
	}

	public void addStats(Stats singleStats) {
		stats.add(singleStats);
	}
}