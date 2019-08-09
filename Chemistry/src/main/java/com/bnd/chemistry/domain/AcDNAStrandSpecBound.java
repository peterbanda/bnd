package com.bnd.chemistry.domain;

import com.bnd.core.domain.ValueBound;

public class AcDNAStrandSpecBound extends ArtificialChemistrySpecBound {

	private ValueBound<Integer> singleStrandsNum = new ValueBound<Integer>();
	private ValueBound<Double> upperToLowerStrandRatio = new ValueBound<Double>();
	private ValueBound<Double> complementaryStrandsRatio = new ValueBound<Double>();

	private ValueBound<Double> upperStrandPartialBindingDistributionShape = new ValueBound<Double>();
	private ValueBound<Double> upperStrandPartialBindingDistributionLocation = new ValueBound<Double>();

	private boolean useGlobalOrder;

	public ValueBound<Integer> getSingleStrandsNum() {
		return singleStrandsNum;
	}

	public void setSingleStrandsNum(ValueBound<Integer> singleStrandsNum) {
		this.singleStrandsNum = singleStrandsNum;
	}

	public Integer getSingleStrandsNumFrom() {
		return singleStrandsNum.getFrom();
	}

	public Integer getSingleStrandsNumTo() {
		return singleStrandsNum.getTo();
	}

	public void setSingleStrandsNumFrom(Integer value) {
		singleStrandsNum.setFrom(value);
	}

	public void setSingleStrandsNumTo(Integer value) {
		singleStrandsNum.setTo(value);
	}

	public ValueBound<Double> getUpperToLowerStrandRatio() {
		return upperToLowerStrandRatio;
	}

	public void setUpperToLowerStrandRatio(ValueBound<Double> upperToLowerStrandRatio) {
		this.upperToLowerStrandRatio = upperToLowerStrandRatio;
	}

	public Double getUpperToLowerStrandRatioFrom() {
		return upperToLowerStrandRatio.getFrom();
	}

	public Double getUpperToLowerStrandRatioTo() {
		return upperToLowerStrandRatio.getTo();
	}

	public void setUpperToLowerStrandRatioFrom(Double value) {
		upperToLowerStrandRatio.setFrom(value);
	}

	public void setUpperToLowerStrandRatioTo(Double value) {
		upperToLowerStrandRatio.setTo(value);
	}

	public ValueBound<Double> getComplementaryStrandsRatio() {
		return complementaryStrandsRatio;
	}

	public void setComplementaryStrandsRatio(ValueBound<Double> complementaryStrandsRatio) {
		this.complementaryStrandsRatio = complementaryStrandsRatio;
	}

	public Double getComplementaryStrandsRatioFrom() {
		return complementaryStrandsRatio.getFrom();
	}

	public Double getComplementaryStrandsRatioTo() {
		return complementaryStrandsRatio.getTo();
	}

	public void setComplementaryStrandsRatioFrom(Double value) {
		complementaryStrandsRatio.setFrom(value);
	}

	public void setComplementaryStrandsRatioTo(Double value) {
		complementaryStrandsRatio.setTo(value);
	}
	
	public ValueBound<Double> getUpperStrandPartialBindingDistributionShape() {
		return upperStrandPartialBindingDistributionShape;
	}

	public void setUpperStrandPartialBindingDistributionShape(ValueBound<Double> upperStrandPartialBindingDistributionShape) {
		this.upperStrandPartialBindingDistributionShape = upperStrandPartialBindingDistributionShape;
	}

	public Double getUpperStrandPartialBindingDistributionShapeFrom() {
		return upperStrandPartialBindingDistributionShape.getFrom();
	}

	public Double getUpperStrandPartialBindingDistributionShapeTo() {
		return upperStrandPartialBindingDistributionShape.getTo();
	}

	public void setUpperStrandPartialBindingDistributionShapeFrom(Double value) {
		upperStrandPartialBindingDistributionShape.setFrom(value);
	}

	public void setUpperStrandPartialBindingDistributionShapeTo(Double value) {
		upperStrandPartialBindingDistributionShape.setTo(value);
	}
	
	public ValueBound<Double> getUpperStrandPartialBindingDistributionLocation() {
		return upperStrandPartialBindingDistributionLocation;
	}

	public void setUpperStrandPartialBindingDistributionLocation(ValueBound<Double> upperStrandPartialBindingDistributionLocation) {
		this.upperStrandPartialBindingDistributionLocation = upperStrandPartialBindingDistributionLocation;
	}

	public Double getUpperStrandPartialBindingDistributionLocationFrom() {
		return upperStrandPartialBindingDistributionLocation.getFrom();
	}

	public Double getUpperStrandPartialBindingDistributionLocationTo() {
		return upperStrandPartialBindingDistributionLocation.getTo();
	}

	public void setUpperStrandPartialBindingDistributionLocationFrom(Double value) {
		upperStrandPartialBindingDistributionLocation.setFrom(value);
	}

	public void setUpperStrandPartialBindingDistributionLocationTo(Double value) {
		upperStrandPartialBindingDistributionLocation.setTo(value);
	}

	public boolean isUseGlobalOrder() {
		return useGlobalOrder;
	}

	public void setUseGlobalOrder(boolean useGlobalOrder) {
		this.useGlobalOrder = useGlobalOrder;
	}
}