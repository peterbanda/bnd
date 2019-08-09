package com.bnd.chemistry.domain;

import java.io.Serializable;

import com.bnd.chemistry.BndChemistryException;

public class AcConcentrationLevel implements Serializable {

	private Double singleValue;
	private Double lowValue;
	private Double highValue;

	public AcConcentrationLevel(Double singleValue) {
		this.singleValue = singleValue;
	}

	public AcConcentrationLevel(Double lowValue, Double highValue) {
		this.lowValue = lowValue;
		this.highValue = highValue;
	}

	public Double getSingleValue() {
		return singleValue;
	}

	public void setSingleValue(Double singleValue) {
		this.singleValue = singleValue;
	}

	public Double getLowValue() {
		return lowValue;
	}

	public void setLowValue(Double lowValue) {
		this.lowValue = lowValue;
	}

	public Double getHighValue() {
		return highValue;
	}

	public void setHighValue(Double highValue) {
		this.highValue = highValue;
	}

	@Override
	public String toString() {
		if (isSingleValue()) {
			return singleValue.toString();
		}
		return lowValue.toString() + "/" + highValue.toString();
	}

	/**
	 * Works only for low/high concentration levels.
	 * @return
	 */
	public Double getMiddleValue() {
		if (lowValue == null || highValue == null) {
			return null;
		}
		return lowValue + (highValue - lowValue) / 2;
	}

	public boolean isSingleValue() {
		if (singleValue != null) {
			if (lowValue != null || highValue != null) {
				throw new BndChemistryException("Single and two value concentrations are defined as the same time.");
			}
			return true;
		}
		return false;
	}
}
