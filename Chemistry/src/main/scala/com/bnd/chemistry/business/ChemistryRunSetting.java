package com.bnd.chemistry.business;

import java.io.Serializable;
import java.util.Collection;

import com.bnd.chemistry.domain.AcSpecies;
import com.bnd.chemistry.domain.IllegalStateEventHandling;

public class ChemistryRunSetting implements Serializable {

	private IllegalStateEventHandling upperThresholdViolationHandling;
	private IllegalStateEventHandling zeroThresholdViolationHandling;
	private IllegalStateEventHandling notANumberConcentrationHandling;
	private Collection<AcSpecies> storeSpeciesHistorySelectionGroup;

	public IllegalStateEventHandling upperThresholdViolationHandling() {
		return upperThresholdViolationHandling;
	}

	public void upperThresholdViolationHandling(IllegalStateEventHandling upperThresholdViolationHandling) {
		this.upperThresholdViolationHandling = upperThresholdViolationHandling;
	}

	public IllegalStateEventHandling zeroThresholdViolationHandling() {
		return zeroThresholdViolationHandling;
	}

	public void zeroThresholdViolationHandling(IllegalStateEventHandling zeroThresholdViolationHandling) {
		this.zeroThresholdViolationHandling = zeroThresholdViolationHandling;
	}

	public IllegalStateEventHandling notANumberConcentrationHandling() {
		return notANumberConcentrationHandling;
	}

	public void notANumberConcentrationHandling(IllegalStateEventHandling notANumberConcentrationHandling) {
		this.notANumberConcentrationHandling = notANumberConcentrationHandling;
	}

	public Collection<AcSpecies> storeSpeciesHistorySelectionGroup() {
		return storeSpeciesHistorySelectionGroup;
	}

	public void storeSpeciesHistorySelectionGroup(Collection<AcSpecies> storeSpeciesHistorySelectionGroup) {
		this.storeSpeciesHistorySelectionGroup = storeSpeciesHistorySelectionGroup;
	}
}