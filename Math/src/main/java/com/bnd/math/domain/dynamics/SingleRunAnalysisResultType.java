package com.bnd.math.domain.dynamics;

public enum SingleRunAnalysisResultType {
	SpatialCorrelations(true),
	TimeCorrelations(true),
	SpatialStationaryPointsPerTime(true),
	TimeStationaryPointsPerTime(true),
	SpatialCumulativeDiffPerTime(true),
	TimeCumulativeDiffPerTime(true),
	SpatialNonlinearityErrors(true),
	TimeNonlinearityErrors(true),
	DerridaResults(true),
	NeighborTimeCorrelations,
	FinalFixedPointsDetected,
	MeanFixedPointsDetected,
	UnboundValuesDetected,
	FinalLyapunovExponents;

	private final boolean holdsStats;

	private SingleRunAnalysisResultType() {
		this(false);
	}

	private SingleRunAnalysisResultType(boolean holdsStats) {
		this.holdsStats = holdsStats;
	}

	public boolean holdsStats() {
		return holdsStats;
	}
}