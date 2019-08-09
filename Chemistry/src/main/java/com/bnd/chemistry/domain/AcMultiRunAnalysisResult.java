package com.bnd.chemistry.domain;

import com.bnd.math.domain.dynamics.MultiRunAnalysisResult;

public class AcMultiRunAnalysisResult extends MultiRunAnalysisResult<Double> {

	@Deprecated
	private ArtificialChemistry ac;
	private AcCompartment compartment;
	private AcSimulationConfig simulationConfig;

	@Deprecated
	public ArtificialChemistry getAc() {
		return ac;
	}

	@Deprecated
	public void setAc(ArtificialChemistry ac) {
		this.ac = ac;
	}

	public AcCompartment getCompartment() {
		return compartment;
	}

	public void setCompartment(AcCompartment compartment) {
		this.compartment = compartment;
	}
}