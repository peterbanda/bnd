package com.bnd.chemistry.domain;

import com.bnd.core.domain.ValueBound;

public class AcSymmetricSpecBound extends ArtificialChemistrySpecBound {

	private ValueBound<Integer> speciesNum = new ValueBound<Integer>();
	private ValueBound<Integer> reactionNum = new ValueBound<Integer>();
	private ValueBound<Integer> reactantsPerReactionNumber = new ValueBound<Integer>();
	private ValueBound<Integer> productsPerReactionNumber = new ValueBound<Integer>();
	private ValueBound<Integer> catalystsPerReactionNumber = new ValueBound<Integer>();
	private ValueBound<Integer> inhibitorsPerReactionNumber = new ValueBound<Integer>();

	public ValueBound<Integer> getSpeciesNum() {
		return speciesNum;
	}

	public void setSpeciesNum(ValueBound<Integer> speciesNum) {
		this.speciesNum = speciesNum;
	}

	public Integer getSpeciesNumFrom() {
		return speciesNum.getFrom();
	}

	public Integer getSpeciesNumTo() {
		return speciesNum.getTo();
	}

	public void setSpeciesNumFrom(Integer value) {
		speciesNum.setFrom(value);
	}

	public void setSpeciesNumTo(Integer value) {
		speciesNum.setTo(value);
	}

	public ValueBound<Integer> getReactionNum() {
		return reactionNum;
	}

	public void setReactionNum(ValueBound<Integer> reactionNum) {
		this.reactionNum = reactionNum;
	}

	public Integer getReactionNumFrom() {
		return reactionNum.getFrom();
	}

	public Integer getReactionNumTo() {
		return reactionNum.getTo();
	}

	public void setReactionNumFrom(Integer value) {
		reactionNum.setFrom(value);
	}

	public void setReactionNumTo(Integer value) {
		reactionNum.setTo(value);
	}

	public ValueBound<Integer> getReactantsPerReactionNumber() {
		return reactantsPerReactionNumber;
	}

	public void setReactantsPerReactionNumber(ValueBound<Integer> reactantsPerReactionNumber) {
		this.reactantsPerReactionNumber = reactantsPerReactionNumber;
	}

	public Integer getReactantsPerReactionNumberFrom() {
		return reactantsPerReactionNumber.getFrom();
	}

	public Integer getReactantsPerReactionNumberTo() {
		return reactantsPerReactionNumber.getTo();
	}

	public void setReactantsPerReactionNumberFrom(Integer value) {
		reactantsPerReactionNumber.setFrom(value);
	}

	public void setReactantsPerReactionNumberTo(Integer value) {
		reactantsPerReactionNumber.setTo(value);
	}

	public ValueBound<Integer> getProductsPerReactionNumber() {
		return productsPerReactionNumber;
	}

	public void setProductsPerReactionNumber(ValueBound<Integer> productsPerReactionNumber) {
		this.productsPerReactionNumber = productsPerReactionNumber;
	}

	public Integer getProductsPerReactionNumberFrom() {
		return productsPerReactionNumber.getFrom();
	}

	public Integer getProductsPerReactionNumberTo() {
		return productsPerReactionNumber.getTo();
	}

	public void setProductsPerReactionNumberFrom(Integer value) {
		productsPerReactionNumber.setFrom(value);
	}

	public void setProductsPerReactionNumberTo(Integer value) {
		productsPerReactionNumber.setTo(value);
	}
	
	public ValueBound<Integer> getCatalystsPerReactionNumber() {
		return catalystsPerReactionNumber;
	}

	public void setCatalystsPerReactionNumber(ValueBound<Integer> catalystsPerReactionNumber) {
		this.catalystsPerReactionNumber = catalystsPerReactionNumber;
	}

	public Integer getCatalystsPerReactionNumberFrom() {
		return catalystsPerReactionNumber.getFrom();
	}

	public Integer getCatalystsPerReactionNumberTo() {
		return catalystsPerReactionNumber.getTo();
	}

	public void setCatalystsPerReactionNumberFrom(Integer value) {
		catalystsPerReactionNumber.setFrom(value);
	}

	public void setCatalystsPerReactionNumberTo(Integer value) {
		catalystsPerReactionNumber.setTo(value);
	}

	public ValueBound<Integer> getInhibitorsPerReactionNumber() {
		return inhibitorsPerReactionNumber;
	}

	public void setInhibitorsPerReactionNumber(ValueBound<Integer> inhibitorsPerReactionNumber) {
		this.inhibitorsPerReactionNumber = inhibitorsPerReactionNumber;
	}

	public Integer getInhibitorsPerReactionNumberFrom() {
		return inhibitorsPerReactionNumber.getFrom();
	}

	public Integer getInhibitorsPerReactionNumberTo() {
		return inhibitorsPerReactionNumber.getTo();
	}

	public void setInhibitorsPerReactionNumberFrom(Integer value) {
		inhibitorsPerReactionNumber.setFrom(value);
	}

	public void setInhibitorsPerReactionNumberTo(Integer value) {
		inhibitorsPerReactionNumber.setTo(value);
	}
}