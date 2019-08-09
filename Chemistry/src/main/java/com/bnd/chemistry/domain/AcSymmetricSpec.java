package com.bnd.chemistry.domain;

public class AcSymmetricSpec extends ArtificialChemistrySpec {

	private Integer speciesNum;
	private Integer reactionNum;
	private Integer reactantsPerReactionNumber;
	private Integer productsPerReactionNumber;
	private Integer catalystsPerReactionNumber;
	private Integer inhibitorsPerReactionNumber;
	
	public Integer getSpeciesNum() {
		return speciesNum;
	}

	public void setSpeciesNum(Integer speciesNum) {
		this.speciesNum = speciesNum;
	}

	public Integer getReactionNum() {
		return reactionNum;
	}

	public void setReactionNum(Integer reactionNum) {
		this.reactionNum = reactionNum;
	}

	public Integer getReactantsPerReactionNumber() {
		return reactantsPerReactionNumber;
	}

	public void setReactantsPerReactionNumber(Integer reactantsPerReactionNumber) {
		this.reactantsPerReactionNumber = reactantsPerReactionNumber;
	}

	public Integer getProductsPerReactionNumber() {
		return productsPerReactionNumber;
	}

	public void setProductsPerReactionNumber(Integer productsPerReactionNumber) {
		this.productsPerReactionNumber = productsPerReactionNumber;
	}

	public Integer getCatalystsPerReactionNumber() {
		return catalystsPerReactionNumber;
	}

	public void setCatalystsPerReactionNumber(Integer catalystsPerReactionNumber) {
		this.catalystsPerReactionNumber = catalystsPerReactionNumber;
	}

	public Integer getInhibitorsPerReactionNumber() {
		return inhibitorsPerReactionNumber;
	}

	public void setInhibitorsPerReactionNumber(Integer inhibitorsPerReactionNumber) {
		this.inhibitorsPerReactionNumber = inhibitorsPerReactionNumber;
	}
}