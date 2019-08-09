package com.bnd.chemistry.business;

import com.bnd.chemistry.domain.IllegalStateEventHandling;

class AcConcentrationLimitHandler {

	private IllegalStateEventHandling upperThresholdViolationHandling = IllegalStateEventHandling.FixSilently;
	private IllegalStateEventHandling zeroThresholdViolationHandling = IllegalStateEventHandling.FixSilently;
	private IllegalStateEventHandling notANumberConcentrationHandling = IllegalStateEventHandling.FixSilently;
	private Double upperThreshold = 10000.0d;
	private Double lowerThreshold;
	private int speciesCount;

	protected AcConcentrationLimitHandler(
		IllegalStateEventHandling upperThresholdViolationHandling,
		IllegalStateEventHandling zeroThresholdViolationHandling,
		IllegalStateEventHandling notANumberConcentrationHandling,
		Double upperThreshold,
		Double lowerThreshold,
		int speciesCount
	) {
		if (upperThresholdViolationHandling != null) {
			this.upperThresholdViolationHandling = upperThresholdViolationHandling;
		}
		if (zeroThresholdViolationHandling != null) { 
			this.zeroThresholdViolationHandling = zeroThresholdViolationHandling;
		}
		if (notANumberConcentrationHandling != null) {
			this.notANumberConcentrationHandling = notANumberConcentrationHandling;
		}
		if (upperThreshold != null) {
			this.upperThreshold = upperThreshold;
		}
		this.lowerThreshold = lowerThreshold;
		this.speciesCount = speciesCount;
	}

	protected void handle(Double[] magnitudes) {
		for (int speciesIndex = 0; speciesIndex < speciesCount; speciesIndex++) {
			if (!handleNotANumberConcentration(magnitudes, speciesIndex)) {
				if (!handleLowerThresholdViolation(magnitudes, speciesIndex)) {
					if (!handleZeroThresholdViolation(magnitudes, speciesIndex)) {
						handleUpperThresholdViolation(magnitudes, speciesIndex);
					}
				}
			}
		}
	}

	private boolean handleNotANumberConcentration(Double[] magnitudes, int speciesIndex) {
		if (magnitudes[speciesIndex].isNaN()) {
			switch (notANumberConcentrationHandling) {
				case FixSilently: magnitudes[speciesIndex] = 0d; break;
				case FixAndCreateEvent: // TODO create event
				case Stop: // TODO throw StopSimulationException and handle that in runSimulation
					break;
				case ThrowException: // TODO throw NotANumberConcentrationException
					break;
			}
			return true;
		}
		return false;
	}

	private boolean handleUpperThresholdViolation(Double[] magnitudes, final int speciesIndex) {
		if (upperThresholdViolationHandling.isIgnore()) {
			return false;
		}
		if (magnitudes[speciesIndex].compareTo(upperThreshold) > 0) {
			switch (upperThresholdViolationHandling) {
				case FixSilently:
					magnitudes[speciesIndex] = upperThreshold; break; 
				case FixAndCreateEvent: // TODO create event
				case Stop: // TODO throw StopSimulationException and handle that in runSimulation
					break;
				case ThrowException: // TODO throw UpperThresholdViolationException
					break;
				default:
					break;
			}
			return true;
		}
		return false;
	}

	private boolean handleLowerThresholdViolation(Double[] magnitudes, final int speciesIndex) {
		if (lowerThreshold == null) {
			return false;
		}
		if (magnitudes[speciesIndex].compareTo(lowerThreshold) < 0) {
			magnitudes[speciesIndex] = 0.0d;
			return true;
		}
		return false;
	}

	private boolean handleZeroThresholdViolation(Double[] magnitudes, final int speciesIndex) {
		if (magnitudes[speciesIndex] < 0) {
			switch (zeroThresholdViolationHandling) {
				case FixSilently: magnitudes[speciesIndex] = 0d; break;
				case FixAndCreateEvent: // TODO create event
				case Stop: // TODO throw StopSimulationException and handle that in runSimulation
					break;
				case ThrowException: // TODO throw ZeroThresholdViolationException
					break;
			}
			return true;
		}
		return false;
	}
}