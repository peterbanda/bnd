package com.bnd.chemistry.domain;

public class AcPerturbationPerformance extends AcEvaluatedPerformance {

	private Integer perturbationNum;
	private Double perturbationStrength;

	public Integer getPerturbationNum() {
		return perturbationNum;
	}

	public void setPerturbationNum(Integer perturbationNum) {
		this.perturbationNum = perturbationNum;
	}

	public Double getPerturbationStrength() {
		return perturbationStrength;
	}

	public void setPerturbationStrength(Double perturbationStrength) {
		this.perturbationStrength = perturbationStrength;
	}
}