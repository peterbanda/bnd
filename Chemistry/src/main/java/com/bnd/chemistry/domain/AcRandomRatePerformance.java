package com.bnd.chemistry.domain;

import java.util.Collection;
import java.util.HashSet;

public class AcRandomRatePerformance extends AcEvaluatedPerformance {

	private Integer randomRateGenerationNum;
	private Collection<AcRateConstantTypeBound> rateConstantTypeBounds = new HashSet<AcRateConstantTypeBound>();

	public Integer getRandomRateGenerationNum() {
		return randomRateGenerationNum;
	}

	public void setRandomRateGenerationNum(Integer randomRateGenerationNum) {
		this.randomRateGenerationNum = randomRateGenerationNum;
	}

	public Collection<AcRateConstantTypeBound> getRateConstantTypeBounds() {
		return rateConstantTypeBounds;
	}

	public void setRateConstantTypeBounds(Collection<AcRateConstantTypeBound> rateConstantTypeBounds) {
		this.rateConstantTypeBounds = rateConstantTypeBounds;
	}

	public void addRateConstantTypeBound(AcRateConstantTypeBound rateConstantTypeBound) {
		rateConstantTypeBounds.add(rateConstantTypeBound);
	}
}