package com.bnd.math.domain.dynamics;

import java.util.Date;
import java.util.List;

import com.bnd.core.domain.um.User;
import com.bnd.math.domain.StatsSequence;

public class SingleRunAnalysisResult {

	private Long id;
	private Date timeCreated = new Date();
	private User createdBy;	

	private List<Double> initialState;
	private StatsSequence spatialCorrelations;
	private StatsSequence timeCorrelations;
	private List<Double> neighborTimeCorrelations;
	private StatsSequence spatialStationaryPointsPerTime;
	private StatsSequence timeStationaryPointsPerTime;
	private StatsSequence spatialCumulativeDiffPerTime;
	private StatsSequence timeCumulativeDiffPerTime;
	private StatsSequence spatialNonlinearityErrors;
	private StatsSequence timeNonlinearityErrors;

	private List<Boolean> finalFixedPointsDetected;
	private List<Double> meanFixedPointsDetected;
	private List<Boolean> unboundValuesDetected;
	private List<Double> finalLyapunovExponents;
	private StatsSequence derridaResults;

	// optional
	private MultiRunAnalysisResult<?> multiRunResult;

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

	public List<Double> getInitialState() {
		return initialState;
	}

	public void setInitialState(List<Double> initialState) {
		this.initialState = initialState;
	}

	public StatsSequence getSpatialCorrelations() {
		return spatialCorrelations;
	}

	public void setSpatialCorrelations(StatsSequence spatialCorrelations) {
		this.spatialCorrelations = spatialCorrelations;
	}

	public StatsSequence getTimeCorrelations() {
		return timeCorrelations;
	}

	public void setTimeCorrelations(StatsSequence timeCorrelations) {
		this.timeCorrelations = timeCorrelations;
	}

	public List<Double> getNeighborTimeCorrelations() {
		return neighborTimeCorrelations;
	}

	public void setNeighborTimeCorrelations(List<Double> neighborTimeCorrelations) {
		this.neighborTimeCorrelations = neighborTimeCorrelations;
	}

	public StatsSequence getSpatialStationaryPointsPerTime() {
		return spatialStationaryPointsPerTime;
	}

	public void setSpatialStationaryPointsPerTime(StatsSequence spatialStationaryPointsPerTime) {
		this.spatialStationaryPointsPerTime = spatialStationaryPointsPerTime;
	}

	public StatsSequence getTimeStationaryPointsPerTime() {
		return timeStationaryPointsPerTime;
	}

	public void setTimeStationaryPointsPerTime(StatsSequence timeStationaryPointsPerTime) {
		this.timeStationaryPointsPerTime = timeStationaryPointsPerTime;
	}

	public StatsSequence getSpatialCumulativeDiffPerTime() {
		return spatialCumulativeDiffPerTime;
	}

	public void setSpatialCumulativeDiffPerTime(StatsSequence spatialCumulativeDiffPerTime) {
		this.spatialCumulativeDiffPerTime = spatialCumulativeDiffPerTime;
	}

	public StatsSequence getTimeCumulativeDiffPerTime() {
		return timeCumulativeDiffPerTime;
	}

	public void setTimeCumulativeDiffPerTime(StatsSequence timeCumulativeDiffPerTime) {
		this.timeCumulativeDiffPerTime = timeCumulativeDiffPerTime;
	}

	public StatsSequence getSpatialNonlinearityErrors() {
		return spatialNonlinearityErrors;
	}

	public void setSpatialNonlinearityErrors(StatsSequence spatialNonlinearityErrors) {
		this.spatialNonlinearityErrors = spatialNonlinearityErrors;
	}

	public StatsSequence getTimeNonlinearityErrors() {
		return timeNonlinearityErrors;
	}

	public void setTimeNonlinearityErrors(StatsSequence timeNonlinearityErrors) {
		this.timeNonlinearityErrors = timeNonlinearityErrors;
	}

	public List<Boolean> getFinalFixedPointsDetected() {
		return finalFixedPointsDetected;
	}

	public void setFinalFixedPointsDetected(List<Boolean> finalFixedPointsDetected) {
		this.finalFixedPointsDetected = finalFixedPointsDetected;
	}

	public List<Double> getMeanFixedPointsDetected() {
		return meanFixedPointsDetected;
	}

	public void setMeanFixedPointsDetected(List<Double> meanFixedPointsDetected) {
		this.meanFixedPointsDetected = meanFixedPointsDetected;
	}

	public List<Boolean> getUnboundValuesDetected() {
		return unboundValuesDetected;
	}

	public void setUnboundValuesDetected(List<Boolean> unboundValuesDetected) {
		this.unboundValuesDetected = unboundValuesDetected;
	}

	public List<Double> getFinalLyapunovExponents() {
		return finalLyapunovExponents;
	}

	public void setFinalLyapunovExponents(List<Double> finalLyapunovExponents) {
		this.finalLyapunovExponents = finalLyapunovExponents;
	}

	public StatsSequence getDerridaResults() {
		return derridaResults;
	}

	public void setDerridaResults(StatsSequence derridaResults) {
		this.derridaResults = derridaResults;
	}

	public MultiRunAnalysisResult<?> getMultiRunResult() {
		return multiRunResult;
	}

	protected void setMultiRunResult(MultiRunAnalysisResult<?> multiRunResult) {
		this.multiRunResult = multiRunResult;
	}
}