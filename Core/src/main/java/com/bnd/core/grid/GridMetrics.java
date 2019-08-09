package com.bnd.core.grid;

public class GridMetrics {

	private int totalCpus;
	private int totalHosts;
	private int totalNodes;
	private int totalActiveJobs;

	private double averageActiveJobs;
	private double averageCpuLoad;
	private double averageIdleTime;
	private double averageJobExecuteTime;
	private double averageJobWaitTime;
	private double averageRejectedJobs;
	private double averageUpTime;

	private long minimumUpTime;

	public int getTotalCpus() {
		return totalCpus;
	}

	public void setTotalCpus(int totalCpus) {
		this.totalCpus = totalCpus;
	}

	public int getTotalHosts() {
		return totalHosts;
	}

	public void setTotalHosts(int totalHosts) {
		this.totalHosts = totalHosts;
	}

	public int getTotalNodes() {
		return totalNodes;
	}

	public void setTotalNodes(int totalNodes) {
		this.totalNodes = totalNodes;
	}

	public int getTotalActiveJobs() {
		return totalActiveJobs;
	}

	public void setTotalActiveJobs(int totalActiveJobs) {
		this.totalActiveJobs = totalActiveJobs;
	}

	public double getAverageActiveJobs() {
		return averageActiveJobs;
	}

	public void setAverageActiveJobs(double averageActiveJobs) {
		this.averageActiveJobs = averageActiveJobs;
	}

	public double getAverageCpuLoad() {
		return averageCpuLoad;
	}

	public void setAverageCpuLoad(double averageCpuLoad) {
		this.averageCpuLoad = averageCpuLoad;
	}

	public double getAverageIdleTime() {
		return averageIdleTime;
	}

	public void setAverageIdleTime(double averageIdleTime) {
		this.averageIdleTime = averageIdleTime;
	}

	public double getAverageJobExecuteTime() {
		return averageJobExecuteTime;
	}

	public void setAverageJobExecuteTime(double averageJobExecuteTime) {
		this.averageJobExecuteTime = averageJobExecuteTime;
	}

	public double getAverageJobWaitTime() {
		return averageJobWaitTime;
	}

	public void setAverageJobWaitTime(double averageJobWaitTime) {
		this.averageJobWaitTime = averageJobWaitTime;
	}

	public double getAverageRejectedJobs() {
		return averageRejectedJobs;
	}

	public void setAverageRejectedJobs(double averageRejectedJobs) {
		this.averageRejectedJobs = averageRejectedJobs;
	}

	public double getAverageUpTime() {
		return averageUpTime;
	}

	public void setAverageUpTime(double averageUpTime) {
		this.averageUpTime = averageUpTime;
	}

	public long getMinimumUpTime() {
		return minimumUpTime;
	}

	public void setMinimumUpTime(long minimumUpTime) {
		this.minimumUpTime = minimumUpTime;
	}
}