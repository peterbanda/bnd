package com.bnd.chemistry.domain;

import com.bnd.math.domain.rand.RandomDistribution;

public class AcDNAStrandSpec extends ArtificialChemistrySpec {

	private Integer singleStrandsNum;
	private Double upperToLowerStrandRatio;
	private Double complementaryStrandsRatio;
	private RandomDistribution<Integer> upperStrandPartialBindingDistribution;
	private boolean mirrorComplementarity;
	private boolean useGlobalOrder;

	public Double getUpperToLowerStrandRatio() {
		return upperToLowerStrandRatio;
	}

	public void setUpperToLowerStrandRatio(Double upperToLowerStrandRatio) {
		this.upperToLowerStrandRatio = upperToLowerStrandRatio;
	}

	public Double getComplementaryStrandsRatio() {
		return complementaryStrandsRatio;
	}

	public void setComplementaryStrandsRatio(Double complementaryStrandsRatio) {
		this.complementaryStrandsRatio = complementaryStrandsRatio;
	}

	public RandomDistribution<Integer> getUpperStrandPartialBindingDistribution() {
		return upperStrandPartialBindingDistribution;
	}

	public void setUpperStrandPartialBindingDistribution(RandomDistribution<Integer> upperStrandPartialBindingDistribution) {
		this.upperStrandPartialBindingDistribution = upperStrandPartialBindingDistribution;
	}

	public boolean isMirrorComplementarity() {
		return mirrorComplementarity;
	}

	public void setMirrorComplementarity(boolean mirrorComplementarity) {
		this.mirrorComplementarity = mirrorComplementarity;
	}

	public Integer getSingleStrandsNum() {
		return singleStrandsNum;
	}

	public void setSingleStrandsNum(Integer singleStrandsNum) {
		this.singleStrandsNum = singleStrandsNum;
	}

	public boolean isUseGlobalOrder() {
		return useGlobalOrder;
	}

	public void setUseGlobalOrder(boolean useGlobalOrder) {
		this.useGlobalOrder = useGlobalOrder;
	}
}